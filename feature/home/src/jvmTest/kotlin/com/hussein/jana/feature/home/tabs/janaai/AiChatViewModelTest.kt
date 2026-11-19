package com.hussein.jana.feature.home.tabs.janaai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [AiChatViewModel].
 *
 * Covers all MVI intents, state transitions, and effects. The
 * [StandardTestDispatcher] controls virtual time so the 1 200 ms AI-reply
 * delay is exercised without wall-clock waiting.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AiChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun buildVm() = AiChatViewModel()

    // ── 1. Initial state ──────────────────────────────────────────────────────

    @Test
    fun `initial state has default messages`() = runTest(testDispatcher) {
        val vm = buildVm()
        assertEquals(defaultMessages(), vm.state.value.messages)
    }

    @Test
    fun `initial state has default quick replies`() = runTest(testDispatcher) {
        val vm = buildVm()
        assertEquals(defaultQuickReplies(), vm.state.value.quickReplies)
    }

    @Test
    fun `initial state has empty inputText`() = runTest(testDispatcher) {
        val vm = buildVm()
        assertEquals("", vm.state.value.inputText)
    }

    @Test
    fun `initial state isTyping is false`() = runTest(testDispatcher) {
        val vm = buildVm()
        assertFalse(vm.state.value.isTyping)
    }

    // ── 2. InputChanged ───────────────────────────────────────────────────────

    @Test
    fun `InputChanged updates inputText`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(AiChatIntent.InputChanged("Hello AI"))
        advanceTimeBy(50)

        assertEquals("Hello AI", vm.state.value.inputText)
    }

    @Test
    fun `InputChanged with empty string clears inputText`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(AiChatIntent.InputChanged("some text"))
        advanceTimeBy(50)
        vm.onIntent(AiChatIntent.InputChanged(""))
        advanceTimeBy(50)

        assertEquals("", vm.state.value.inputText)
    }

    // ── 3. SendMessage ────────────────────────────────────────────────────────

    @Test
    fun `SendMessage adds user message to messages list`() = runTest(testDispatcher) {
        val vm = buildVm()
        val initialCount = vm.state.value.messages.size

        vm.onIntent(AiChatIntent.InputChanged("What is the discriminant?"))
        advanceTimeBy(50)
        vm.onIntent(AiChatIntent.SendMessage)
        advanceTimeBy(50)

        val messages = vm.state.value.messages
        assertTrue(messages.size > initialCount)
        val lastUserMsg = messages.lastOrNull { it.isFromUser }
        assertNotNull(lastUserMsg)
        assertEquals("What is the discriminant?", lastUserMsg.content)
    }

    @Test
    fun `SendMessage clears inputText after sending`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(AiChatIntent.InputChanged("Test message"))
        advanceTimeBy(50)
        vm.onIntent(AiChatIntent.SendMessage)
        advanceTimeBy(50)

        assertEquals("", vm.state.value.inputText)
    }

    @Test
    fun `SendMessage sets isTyping to true immediately`() = runTest(testDispatcher) {
        val vm = buildVm()
        vm.onIntent(AiChatIntent.InputChanged("Hello"))
        advanceTimeBy(50)
        vm.onIntent(AiChatIntent.SendMessage)
        advanceTimeBy(50) // before the 1200ms delay

        assertTrue(vm.state.value.isTyping)
    }

    // ── 4. Blank send is ignored ──────────────────────────────────────────────

    @Test
    fun `SendMessage with blank inputText does not add a message`() = runTest(testDispatcher) {
        val vm = buildVm()
        val initialCount = vm.state.value.messages.size

        vm.onIntent(AiChatIntent.SendMessage) // inputText is "" by default
        advanceTimeBy(100)

        assertEquals(initialCount, vm.state.value.messages.size)
        assertFalse(vm.state.value.isTyping)
    }

    @Test
    fun `SendMessage with whitespace-only inputText is ignored`() = runTest(testDispatcher) {
        val vm = buildVm()
        val initialCount = vm.state.value.messages.size

        vm.onIntent(AiChatIntent.InputChanged("   "))
        advanceTimeBy(50)
        vm.onIntent(AiChatIntent.SendMessage)
        advanceTimeBy(100)

        assertEquals(initialCount, vm.state.value.messages.size)
        assertFalse(vm.state.value.isTyping)
    }

    // ── 5. AI reply after delay ───────────────────────────────────────────────

    @Test
    fun `AI reply is added after 1200ms delay`() = runTest(testDispatcher) {
        val vm = buildVm()
        val initialCount = vm.state.value.messages.size

        vm.onIntent(AiChatIntent.InputChanged("Let me think..."))
        advanceTimeBy(50)
        vm.onIntent(AiChatIntent.SendMessage)
        advanceTimeBy(1_300) // past the 1200ms delay

        // Should have user message + AI reply
        val messages = vm.state.value.messages
        assertTrue(messages.size >= initialCount + 2)
        val lastMsg = messages.last()
        assertFalse(lastMsg.isFromUser)
        assertEquals("Great thinking! Let's continue working through this step by step.", lastMsg.content)
    }

    @Test
    fun `isTyping becomes false after AI reply arrives`() = runTest(testDispatcher) {
        val vm = buildVm()

        vm.onIntent(AiChatIntent.InputChanged("Test"))
        advanceTimeBy(50)
        vm.onIntent(AiChatIntent.SendMessage)
        advanceTimeBy(1_300)

        assertFalse(vm.state.value.isTyping)
    }

    @Test
    fun `ScrollToBottom effect is emitted after AI reply`() = runTest(testDispatcher) {
        val vm = buildVm()
        val effects = mutableListOf<AiChatEffect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.onIntent(AiChatIntent.InputChanged("Hello"))
        advanceTimeBy(50)
        vm.onIntent(AiChatIntent.SendMessage)
        advanceTimeBy(1_300)

        assertTrue(effects.any { it is AiChatEffect.ScrollToBottom })
        collector.cancel()
    }

    // ── 6. MicPressed ─────────────────────────────────────────────────────────

    @Test
    fun `MicPressed emits ShowSnackbar effect`() = runTest(testDispatcher) {
        val vm = buildVm()
        val effects = mutableListOf<AiChatEffect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.onIntent(AiChatIntent.MicPressed)
        advanceTimeBy(100)

        val effect = effects.firstOrNull()
        assertTrue(effect is AiChatEffect.ShowSnackbar)
        assertEquals("Voice input coming soon", (effect as AiChatEffect.ShowSnackbar).message)
        collector.cancel()
    }

    // ── 7. QuickReplySelected ─────────────────────────────────────────────────

    @Test
    fun `QuickReplySelected with valid id fills inputText and sends`() = runTest(testDispatcher) {
        val vm = buildVm()
        val initialCount = vm.state.value.messages.size
        val firstReply = vm.state.value.quickReplies.first()

        vm.onIntent(AiChatIntent.QuickReplySelected(firstReply.id))
        advanceTimeBy(100)

        // Message should have been sent (inputText cleared)
        assertEquals("", vm.state.value.inputText)
        val userMessages = vm.state.value.messages.filter { it.isFromUser }
        assertTrue(userMessages.isNotEmpty())
        assertEquals(firstReply.label, userMessages.last().content)
    }

    @Test
    fun `QuickReplySelected with unknown id does nothing`() = runTest(testDispatcher) {
        val vm = buildVm()
        val initialCount = vm.state.value.messages.size

        vm.onIntent(AiChatIntent.QuickReplySelected("nonexistent_id"))
        advanceTimeBy(100)

        // No message added, state unchanged
        assertEquals(initialCount, vm.state.value.messages.size)
        assertFalse(vm.state.value.isTyping)
    }

    // ── 8. QuickActionClicked ─────────────────────────────────────────────────

    @Test
    fun `QuickActionClicked emits ShowSnackbar effect`() = runTest(testDispatcher) {
        val vm = buildVm()
        val effects = mutableListOf<AiChatEffect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.onIntent(AiChatIntent.QuickActionClicked("a1"))
        advanceTimeBy(100)

        val effect = effects.firstOrNull()
        assertTrue(effect is AiChatEffect.ShowSnackbar)
        assertTrue((effect as AiChatEffect.ShowSnackbar).message.contains("a1"))
        collector.cancel()
    }

    // ── 9. MenuClicked ────────────────────────────────────────────────────────

    @Test
    fun `MenuClicked emits ShowSnackbar effect`() = runTest(testDispatcher) {
        val vm = buildVm()
        val effects = mutableListOf<AiChatEffect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.onIntent(AiChatIntent.MenuClicked)
        advanceTimeBy(100)

        val effect = effects.firstOrNull()
        assertTrue(effect is AiChatEffect.ShowSnackbar)
        assertEquals("Chat history coming soon", (effect as AiChatEffect.ShowSnackbar).message)
        collector.cancel()
    }

    // ── 10. AI reply badge ────────────────────────────────────────────────────

    @Test
    fun `AI reply has Socratic Guide badge`() = runTest(testDispatcher) {
        val vm = buildVm()

        vm.onIntent(AiChatIntent.InputChanged("What comes next?"))
        advanceTimeBy(50)
        vm.onIntent(AiChatIntent.SendMessage)
        advanceTimeBy(1_300)

        val lastMsg = vm.state.value.messages.last()
        assertFalse(lastMsg.isFromUser)
        assertEquals("Socratic Guide", lastMsg.badge)
    }

    // ── 11. Message IDs are unique ────────────────────────────────────────────

    @Test
    fun `consecutive messages have unique IDs`() = runTest(testDispatcher) {
        val vm = buildVm()

        vm.onIntent(AiChatIntent.InputChanged("First"))
        advanceTimeBy(50)
        vm.onIntent(AiChatIntent.SendMessage)
        advanceTimeBy(1_300) // include AI reply

        vm.onIntent(AiChatIntent.InputChanged("Second"))
        advanceTimeBy(50)
        vm.onIntent(AiChatIntent.SendMessage)
        advanceTimeBy(1_300)

        val allIds = vm.state.value.messages.map { it.id }
        // IDs from new messages (default messages use "1","2","3")
        val newIds = allIds.drop(defaultMessages().size)
        assertEquals(newIds.size, newIds.distinct().size)
    }

    // ── 12. Multiple effects ──────────────────────────────────────────────────

    @Test
    fun `multiple intents produce multiple effects`() = runTest(testDispatcher) {
        val vm = buildVm()
        val effects = mutableListOf<AiChatEffect>()
        val collector = launch { vm.effect.collect { effects.add(it) } }

        vm.onIntent(AiChatIntent.MicPressed)
        vm.onIntent(AiChatIntent.MenuClicked)
        advanceTimeBy(200)

        assertEquals(2, effects.size)
        collector.cancel()
    }

    // ── 13. State snapshot after send ─────────────────────────────────────────

    @Test
    fun `state after send has trimmed message content`() = runTest(testDispatcher) {
        val vm = buildVm()

        vm.onIntent(AiChatIntent.InputChanged("  spaces around  "))
        advanceTimeBy(50)
        vm.onIntent(AiChatIntent.SendMessage)
        advanceTimeBy(100)

        val userMessages = vm.state.value.messages.filter { it.isFromUser }
        assertEquals("spaces around", userMessages.last().content)
    }
}
