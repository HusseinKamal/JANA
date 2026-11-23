# Locked courses with subscribe & pay checkout (pass 2)

Unsubscribed courses show as locked in the Courses tab and in Course Details, and students buy them through a bottom-sheet checkout that unlocks a course only after the backend verifies the payment. This pass checks the fixes for the three pass-1 findings. The jvmTest suite now covers the list mapping, reactive refresh, auto-open, the locked-lesson effect, unlock-after-verify ordering, dismiss guard, and FAILED/PENDING retry. Retry after PENDING now re-verifies the same session. Locked courses without a price no longer offer Subscribe. The coder's evidence reports `:feature:home:jvmTest` at 127 tests, 0 failures, and a successful `:androidApp:assembleDebug`.

Watch for: if verify returns SUCCESS but the course re-fetch fails, the session id is already cleared, so Retry opens a new checkout for a course the student has already paid for (**confirmed**, non-blocking while the backend is mocked). While a PENDING session is held, the student cannot start a fresh checkout or change the payment method until the screen is recreated (**likely**, non-blocking).

**Verdict**: APPROVED

## High-level view

All three pass-1 findings are fixed. The new tests cover review check (g). They hold verify open with a `CompletableDeferred` and assert the course stays locked during PROCESSING and VERIFYING, which pins the plan's "unlock only after verify" rule.

`CourseDetailViewModel` now remembers the session it last verified in `pendingSessionId` until the backend gives a final answer. This prevents a second charge after PENDING or a network error. The variable is cleared too early on one path, and on another path it never gets cleared.

`Course.isPurchasable` (`!isSubscribed && price != null`) gates the card's Subscribe button, the details bottom bar and both ways of opening the sheet. An unpriced course still shows as locked, and a missing price is never treated as free.

<details>
<summary>Issues (2)</summary>

1. **Paid-but-refetch-failed retry creates a new checkout**: `pendingSessionId` is cleared as soon as verify returns SUCCESS, before the course re-fetch. If the re-fetch fails, Retry calls `createCheckout` again. Keep the session until the re-fetch confirms `isSubscribed`, or have Retry in that state only re-fetch the course.
2. **PENDING session cannot be abandoned**: Once a session is PENDING, every Pay or Retry re-verifies it. The method picker stays enabled but its selection is ignored, and the button still reads "Pay X" even though it only re-verifies. Clear `pendingSessionId` on `DismissCheckout` or when the method changes, or show a "check status" label while a session is pending.

</details>

<details>
<summary>Details</summary>

### Session tracking in `verify()`

```
startPayment ──pendingSessionId?──yes──► VERIFYING ─► verify(sameId)
      │ no
      ▼
PROCESSING ─► createCheckout ─► VERIFYING ─► verify(newId)

verify: pendingSessionId = id
        network error ─────────────► FAILED (id kept → retry re-verifies)
        PENDING ───────────────────► FAILED(PENDING) (id kept)
        SUCCESS/FAILED ─► id = null ─┬─ FAILED ──────────────► FAILED (retry = new checkout)
                                     └─ SUCCESS ─► refetch ─┬─ subscribed ─► SUCCESS
                                                            └─ fails ──────► FAILED (id already null)
```

The bottom branch is the problem (**confirmed**). `if (verification.status != PaymentStatus.PENDING) pendingSessionId = null` runs before `getCourseDetail(courseId)`. If the re-fetch then fails or returns a stale `isSubscribed = false`, the sheet shows "Payment failed" with Retry. Retry calls `createCheckout` again, but the backend has already confirmed the payment. The mock store happily creates a second session for a course that is already subscribed, so the mock cannot show this. A real gateway would charge twice. This is the same double-charge risk as pass-1 finding 2, just on the path after success.

The opposite case is that `pendingSessionId` never gets released (**likely**). `dismissCheckout()` resets `CheckoutUiState` but leaves `pendingSessionId` alone, and `selectMethod` is still allowed in SELECTING_METHOD and FAILED. A student whose wallet payment stays PENDING, perhaps because they abandoned it, can close the sheet, reopen it, pick Card and tap "Pay 450 EGP". That only re-verifies the old wallet session. The chosen method is ignored, and no new checkout is possible until the ViewModel is recreated. Whether the session should ever be abandoned is a product decision. Right now the UI suggests the student can change the method when they cannot.

### Test coverage

Not tested: re-fetch failure after verify SUCCESS (issue 1), a verify network error followed by retry, dismiss/reopen while a session is pending (issue 2), and the `isPurchasable` gating for unpriced locked courses. The user asked for no tests on that gating, so it is not a finding.

</details>

<details>
<summary>File map</summary>

Changes since pass 1:
- `courses/domain/model/Course.kt`: `isPurchasable`, price KDoc says null is never free
- `courses/presentation/detail/CourseDetailViewModel.kt`: `pendingSessionId`, extracted `verify()`, `isPurchasable` guards on auto-open and Subscribe
- `courses/presentation/detail/CourseDetailScreen.kt`: subscribe bar gated on `isPurchasable`
- `courses/presentation/list/components/JanaCourseCard.kt`: Subscribe button gated on `isPurchasable`
- `feature/home/src/jvmTest/.../tabs/courses/CourseFakes.kt`, `CoursesViewModelTest.kt`, `CourseDetailViewModelTest.kt`, `CoursePaymentRepositoryImplTest.kt`: new

Full diff: `git -C d:\AndroidApps\JANA\JANA diff d78a8fe HEAD`. Pass-2 delta: `git -C d:\AndroidApps\JANA\JANA diff 2d1e861 HEAD`.

</details>
