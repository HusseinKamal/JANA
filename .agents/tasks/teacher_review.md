# Teacher Profile tab with courses, live-lesson subscription and Teacher Chat (pass 3)

This pass reviews commit `19e5d75`, which addresses the three findings from pass 2 on top of the Teacher tab work reviewed earlier (`b863de0`, `8b77633`, `e133572`). The app manifest now declares `INTERNET`, so the teacher photo can load on Android. Both checkout ViewModels keep the paid session id until the re-fetch confirms the subscription. The chat back button goes through `TeacherChatIntent.Back`. The iteration-2 build evidence in `teacher_impl.md` covers all four verification commands (exit 0, 127 tests passing).

Watch for: no new concerns, and no prior finding remains open.

**Verdict**: APPROVED

## High-level view

The INTERNET permission was the only missing piece for the photo. The Coil Ktor fetcher and the OkHttp engine were already on the classpath.

`TeacherProfileViewModel.verify` now clears `pendingSessionId` only when the re-fetched subscription reports ACTIVE or when the backend returns a final non-success answer. A verified payment followed by a failed or stale re-fetch now leads Retry to re-verify the same session rather than open a second checkout. Because the session id is held in the ViewModel, dismissing the sheet and tapping Subscribe again also re-verifies it instead of charging again. `CourseDetailViewModel` received the identical change. It is base-branch code, but the change keeps the two checkouts consistent, as pass 2 suggested.

Teacher Chat back navigation now goes button → `TeacherChatIntent.Back` → `NavigateBack` effect → `onBack`, and `TeacherChatContent` no longer takes an `onBack` parameter.

<details>
<summary>Issues (0)</summary>

No open issues.

</details>

<details>
<summary>File map</summary>

- `androidApp/src/main/AndroidManifest.xml`: `INTERNET` permission.
- `feature/home/.../tabs/teacher/presentation/profile/TeacherProfileViewModel.kt`: paid session kept until the re-fetch confirms ACTIVE.
- `feature/home/.../tabs/courses/presentation/detail/CourseDetailViewModel.kt`: same session-retention fix for course checkout.
- `feature/home/.../tabs/teacher/presentation/chat/TeacherChatScreen.kt`: back button sends `TeacherChatIntent.Back`; `onBack` removed from `TeacherChatContent`.

Earlier commits (unchanged since pass 2): designsystem shared components (`JanaCourseCard` + UI model, `JanaAvatar`/`JanaProfileHeader`/`JanaNavigationCard`, `JanaSubscriptionStatusCard`, `JanaChatBubble`, state views), EN/AR strings, `HomeRoute.TeacherChat`, teacher domain/data/presentation packages, `teacherModule`, and the `itemId`/`itemTitle` renames.

Full diff: `git diff 02d0f61` on `develop`; this pass: `git show 19e5d75`.

</details>
