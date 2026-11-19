# JANA AI Learning Chat screen implementation

This change replaces the placeholder `JanaAiTabScreen` with a fully-realized Socratic AI chat screen. The implementation delivers the complete MVI stack — `AiChatContract`, `AiChatViewModel`, `JanaAiTabScreen`, and seven Compose components — plus a shared `JanaChatInput` bar in `core/designsystem`. Snackbar messages use a typed `AiSnackbarMessage` sealed interface so localization resolution stays in the UI layer, not the ViewModel. The 1 200 ms AI-reply delay is exercised through virtual time via `StandardTestDispatcher`, and 23/23 ViewModel tests pass with no failures.

Watch for: hardcoded English `contentDescription` values on icon buttons in `JanaChatInput` ("Voice input", "Send message") and `AiChatHeader` ("Chat menu") — these bypass the Arabic/English string resource system and will surface as English to TalkBack users regardless of locale. **confirmed**

**Verdict**: CHANGES_REQUESTED

---

## High-level view

`AiSnackbarMessage` as a sealed interface keeps the ViewModel free of UI string concerns — the UI resolves typed keys to localized text before showing the snackbar. `AiMessage` carries the full set of rich-layout fields (`equationDisplay`, `highlightPhrase`, `content2`, `nextStepPrompt`) with all optional sections null by default.

The `ScrollToBottom` effect fires after the AI reply is appended to state; the screen's `LaunchedEffect` reads `totalItemsCount` at that moment. Because Compose recomposition is asynchronous, there is a possible window where the count hasn't yet included the new message, causing the scroll to land on the second-to-last item — not confirmed to trigger in practice given the 1 200 ms delay, but worth a one-frame `delay(0)` guard.

The i18n coverage is complete for visible text: 25 `ai_*` keys across both locale files, format strings for session context, and an accessibility `contentDescription` on the typing indicator via `ai_typing_indicator`. The gap is the icon button labels in `JanaChatInput` ("Voice input", "Send message") and `AiChatHeader` ("Chat menu") — all three are hardcoded English outside the resource system, confirmed.

The `quickActions` grid uses `chunked(2)` to render any count in rows of two. The `when` expressions in `AiSessionBanner` resolve `titleKey`/`subtitleKey` without reflection, but the `else -> key` branch silently renders the raw resource key name on screen if a new action is added without updating the resolver.

---

<details>
<summary>Issues (3)</summary>

1. **JanaChatInput contentDescriptions not localized** — `contentDescription = "Voice input"` and `contentDescription = "Send message"` are hardcoded English in `core/designsystem/JanaChatInput.kt`. Add `ai_mic_cd` / `ai_send_cd` (or generic `input_mic_cd` / `input_send_cd`) to both locale files and pass them in, or accept them as parameters so callers can supply localized strings.
2. **AiChatHeader contentDescription not localized** — `contentDescription = "Chat menu"` on the `MoreVert` icon button in `AiChatHeader.kt` is hardcoded English. Add `ai_menu_cd` to both locale files and use `stringResource`.
3. **`statsText` bypasses i18n** — `val statsText: String = "16 Sessions • 3 Active Subjects"` is an English string baked into the default state and rendered as a UI label in `AiChatHeader`. As mock data this is low-risk, but it should become a format string resource (`ai_stats_format` with count placeholders) before this field is populated from real data.

</details>

---

<details>
<summary>Details</summary>

### Hardcoded accessibility labels in core components

`JanaChatInput` in `core/designsystem` has `contentDescription = "Voice input"` on the mic `IconButton` and `contentDescription = "Send message"` on the send `IconButton`. `AiChatHeader` has `contentDescription = "Chat menu"` on the overflow `IconButton`. All three are outside the compose-resources string system, so Arabic TalkBack users hear English for these controls regardless of device locale.

The idiomatic fix for `JanaChatInput` (a shared component) is to accept `micContentDescription` and `sendContentDescription` as parameters so callers supply localized strings. `AiChatHeader` is feature-specific and should use `stringResource(Res.string.ai_menu_cd)` once that key is added to both locale files, following the same pattern used for `ai_typing_indicator`.

### `when` resolver `else` branch silently renders raw key names

`actionTitleString` and `actionSubtitleString` in `AiSessionBanner` both fall through to `else -> key`. If a future action introduces a key not listed in those `when` expressions, the UI renders the resource key name itself (e.g., `"ai_action_new_key"`) with no compile-time or runtime signal. Adding any new action requires updating both resolver functions or the raw key leaks to the screen.

### `ScrollToBottom` reads list count before recomposition

The effect handler reads `lazyListState.layoutInfo.totalItemsCount` at the moment the effect fires. If Compose hasn't recomposed yet with the new AI message, the count is still one short, so `animateScrollToItem(count - 1)` scrolls to the second-to-last item. A `delay(0)` before the scroll, or gating on `snapshotFlow { lazyListState.layoutInfo.totalItemsCount }`, eliminates the race.

### Test coverage

The 23 tests cover all MVI intents, state transitions, and effects: blank/whitespace send guard, virtual-time delay for AI reply, `ScrollToBottom` effect emission, typed snackbar message types, quick-reply pre-fill + send, unknown reply ID no-op, and unique ID generation across consecutive sends.

Not tested: the `else` branch of the `when` resolvers in `AiSessionBanner` (requires a Compose test to verify the key name renders rather than crashes), and an explicit assertion that `QuickActionClicked` with an unknown action ID still produces `ActionComingSoon` (the guard is in the ViewModel's `when` on `intent`, which covers all `QuickActionClicked` values uniformly — a quick note-to-self for a future test).

The `jvmTest` source set rather than `commonTest` is an accepted KMP constraint; `Dispatchers.setMain` is JVM-only. iOS and native targets run the same ViewModel code without virtual-time coverage until multiplatform coroutine test dispatchers are stable.

</details>

---

<details>
<summary>File map</summary>

| File | Change |
|------|--------|
| `core/designsystem/.../JanaChatInput.kt` | New shared stateless chat input bar; mic optional, send gated on non-blank text |
| `feature/home/.../tabs/janaai/AiChatContract.kt` | New MVI contract: `AiMessage`, `QuickReply`, `AiQuickAction`, `AiChatState`, intents, `AiSnackbarMessage`, effects |
| `feature/home/.../tabs/janaai/AiChatViewModel.kt` | New ViewModel: KMP-safe ID counter, 1 200 ms AI-reply delay, all intent handlers |
| `feature/home/.../tabs/janaai/JanaAiTabScreen.kt` | Replaced placeholder with full stateful + stateless screen decomposition |
| `feature/home/.../tabs/janaai/components/AiChatHeader.kt` | New sticky header: brand, RAG status, badge pills |
| `feature/home/.../tabs/janaai/components/AiSessionBanner.kt` | New teal session-looming card with 2×2 quick-action grid |
| `feature/home/.../tabs/janaai/components/AiMessageBubble.kt` | New AI message bubble + `AiTypingIndicator` with animated 3-dot wave |
| `feature/home/.../tabs/janaai/components/UserMessageBubble.kt` | New right-aligned user bubble (80% max width) |
| `feature/home/.../tabs/janaai/components/QuickRepliesRow.kt` | New horizontally-scrollable suggestion chips |
| `feature/home/.../di/HomeModule.kt` | Added `viewModel { AiChatViewModel() }` |
| `core/designsystem/.../values/strings.xml` | Added 25 `ai_*` string keys |
| `core/designsystem/.../values-ar/strings.xml` | Added 25 Arabic `ai_*` translations |
| `feature/home/src/jvmTest/.../AiChatViewModelTest.kt` | New 23-test suite covering all intents, state transitions, and effects |

Full diff: `git diff main -- feature/home core/designsystem`

</details>
