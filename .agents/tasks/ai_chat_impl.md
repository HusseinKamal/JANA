# JANA AI Learning Chat — Implementation Log

## Task
Implement the full JANA AI Learning Chat screen replacing the placeholder `JanaAiTabScreen.kt`.

## Status
✅ ITERATION 2 COMPLETE — All 5 review findings fixed. Build SUCCESS, 23/23 tests passing.

---

## Review Findings Fixed (Iteration 2)

### Finding 1 — Dead snackbar resource keys
Added `AiSnackbarMessage` sealed interface (`VoiceComingSoon`, `HistoryComingSoon`, `ActionComingSoon`) in `AiChatContract.kt`.
Changed `AiChatEffect.ShowSnackbar(message: String)` → `ShowSnackbar(messageType: AiSnackbarMessage)`.
`AiChatViewModel` now emits typed keys instead of raw English literals.
`JanaAiTabScreen` pre-fetches the three strings via `stringResource()` in the Composable scope and resolves them in the `LaunchedEffect` effect handler.
Updated 3 tests (MicPressed, QuickActionClicked, MenuClicked) to assert on `messageType` enum values.

### Finding 2 — Hardcoded "YOUR NEXT STEP" in AiMessageBubble
Added `ai_next_step_label` / Arabic equivalent to both locale files.
`AiMessageBubble` now uses `stringResource(Res.string.ai_next_step_label)`.

### Finding 3 — Hardcoded English session banner strings
Added three format-string resources: `ai_session_looming` (`%1$s` for time), `ai_session_greeting` (`%1$s` name, `%2$s` subject, `%3$s` teacher), `ai_session_focus` (`%1$s` focus text) to both locale files.
`AiSessionBanner` now calls `stringResource(Res.string.ai_session_looming, state.sessionTime)` etc.

### Finding 4 — AiTypingIndicator missing accessibility content description
Added `ai_typing_indicator` string resource to both locale files.
Outer `Row` in `AiTypingIndicator` now has `Modifier.semantics { contentDescription = stringResource(Res.string.ai_typing_indicator) }`.

### Finding 5 — Silent grid suppression when quickActions.size < 4
Replaced `if (actions.size >= 4) { ... }` with `if (actions.isNotEmpty()) { actions.chunked(2).forEachIndexed { ... } }`.
Grid now renders any number of actions in rows of 2, padding the last row with a `Spacer(weight(1f))` if needed.

---

## Files Created / Modified

### New Files

| Path | Description |
|------|-------------|
| `core/designsystem/src/commonMain/kotlin/.../JanaChatInput.kt` | Reusable stateless chat input bar (Part 1) |
| `feature/home/.../tabs/janaai/AiChatContract.kt` | MVI contract: `AiChatState`, `AiChatIntent`, `AiChatEffect`, data models, default factories |
| `feature/home/.../tabs/janaai/AiChatViewModel.kt` | ViewModel extending `BaseMviViewModel`, KMP-safe ID counter, 1 200 ms AI-reply delay |
| `feature/home/.../tabs/janaai/components/AiChatHeader.kt` | Sticky header: JANA AI brand, RAG status, badge pills |
| `feature/home/.../tabs/janaai/components/AiSessionBanner.kt` | Teal session-looming card with 2×2 quick-action grid |
| `feature/home/.../tabs/janaai/components/AiMessageBubble.kt` | Left-aligned AI bubble + `AiTypingIndicator` with animated dots |
| `feature/home/.../tabs/janaai/components/UserMessageBubble.kt` | Right-aligned user bubble (80% max width) |
| `feature/home/.../tabs/janaai/components/QuickRepliesRow.kt` | Horizontally-scrollable suggestion chips |
| `feature/home/src/jvmTest/.../tabs/janaai/AiChatViewModelTest.kt` | 23 unit tests for the ViewModel |

### Modified Files

| Path | Change |
|------|--------|
| `feature/home/.../tabs/janaai/JanaAiTabScreen.kt` | **Replaced** placeholder with full MVI screen + stateless content |
| `feature/home/.../di/HomeModule.kt` | Added `viewModel { AiChatViewModel() }` |
| `core/designsystem/.../values/strings.xml` | Added 20 AI Chat string keys |
| `core/designsystem/.../values-ar/strings.xml` | Added 20 Arabic AI Chat string keys |

---

## Architecture Decisions

### KMP-safe unique IDs
`System.currentTimeMillis()` is JVM-only. Used an instance counter `private var msgIdCounter = 0` in the ViewModel. Since ViewModels are accessed on the main thread this is safe.

### Scroll-to-bottom
`AiChatEffect.ScrollToBottom` is handled in `JanaAiTabScreen` which owns the `LazyListState`. The effect handler uses `lazyListState.layoutInfo.totalItemsCount - 1` to compute the actual last index regardless of how many items are in the LazyColumn.

### String resource action key mapping
`AiSessionBanner` uses explicit `when` expressions (`actionTitleString` / `actionSubtitleString`) to map `AiQuickAction.titleKey` / `subtitleKey` to `Res.string.*` without reflection.

### Testable delay
`viewModelScope.launch { delay(1_200L) ... }` in `AiChatViewModel.sendUserMessage()` is controlled by `StandardTestDispatcher` via `Dispatchers.setMain` in tests, enabling full virtual-time testing without wall-clock waiting.

### Typography
Newsreader (serif) font used in `AiSessionBanner` greeting text via `MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif)`, consistent with how the design system's `SerifFamily` is applied to headings.

---

## Build & Test Commands

```
# Full JVM compile
.\gradlew :composeApp:compileKotlinJvm

# Feature unit tests
.\gradlew :feature:home:jvmTest
```

### Results (Iteration 2)

```
# :composeApp:compileKotlinJvm
BUILD SUCCESSFUL in 25s
58 actionable tasks: 13 executed, 45 up-to-date

# :feature:home:jvmTest
BUILD SUCCESSFUL in 10s
35 actionable tasks: 2 executed, 33 up-to-date

# Test suite: AiChatViewModelTest
tests="23" failures="0" errors="0" skipped="0"
```

### Test coverage (23 tests)

| # | Test name |
|---|-----------|
| 1 | `initial state has default messages` |
| 2 | `initial state has default quick replies` |
| 3 | `initial state has empty inputText` |
| 4 | `initial state isTyping is false` |
| 5 | `InputChanged updates inputText` |
| 6 | `InputChanged with empty string clears inputText` |
| 7 | `SendMessage adds user message to messages list` |
| 8 | `SendMessage clears inputText after sending` |
| 9 | `SendMessage sets isTyping to true immediately` |
| 10 | `SendMessage with blank inputText does not add a message` |
| 11 | `SendMessage with whitespace-only inputText is ignored` |
| 12 | `AI reply is added after 1200ms delay` |
| 13 | `isTyping becomes false after AI reply arrives` |
| 14 | `ScrollToBottom effect is emitted after AI reply` |
| 15 | `MicPressed emits ShowSnackbar effect` |
| 16 | `QuickReplySelected with valid id fills inputText and sends` |
| 17 | `QuickReplySelected with unknown id does nothing` |
| 18 | `QuickActionClicked emits ShowSnackbar effect` |
| 19 | `MenuClicked emits ShowSnackbar effect` |
| 20 | `AI reply has Socratic Guide badge` |
| 21 | `consecutive messages have unique IDs` |
| 22 | `multiple intents produce multiple effects` |
| 23 | `state after send has trimmed message content` |
