package cafe.adriel.broker

import cafe.adriel.broker.util.TestCoroutineScopeRule
import io.mockk.coVerify
import io.mockk.slot
import io.mockk.spyk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class BrokerTest : GlobalBroker.Publisher, GlobalBroker.Subscriber {

    @get:Rule
    val testScopeRule = TestCoroutineScopeRule()

    val listenerSuccess = spyk(::onEventSuccess)
    val listenerThrowError = spyk(::onEventThrowError)
    val listenerCatchError = spyk(::onEventCatchError)

    @Before
    fun setup() {
        unsubscribe()
    }

    @Test
    fun `when publish event with subscriber then deliver it`() {
        val event = TestEvent("It works!")
        subscribe(testScopeRule, testScopeRule.dispatcher, onEvent = listenerSuccess)
        publish(event)

        coVerify { listenerSuccess(event) }
    }

    @Test
    fun `when publish event without subscriber then do not deliver it`() {
        subscribe(testScopeRule, testScopeRule.dispatcher, onEvent = listenerSuccess)
        publish(UnsubscribedEvent)

        coVerify(inverse = true) { listenerSuccess(any()) }
    }

    @Test
    fun `when subscriber throws exception then publish exception event`() {
        val exceptionEventSlot = slot<BrokerExceptionEvent>()
        val event = TestEvent("Ops!")
        subscribe(testScopeRule, testScopeRule.dispatcher, onEvent = listenerThrowError)
        subscribe(testScopeRule, testScopeRule.dispatcher, onEvent = listenerCatchError)
        publish(event)

        coVerify { listenerThrowError(event) }
        coVerify { listenerCatchError(capture(exceptionEventSlot)) }

        expectThat(exceptionEventSlot.captured) {
            get(BrokerExceptionEvent::subscriber) isEqualTo this@BrokerTest
            get(BrokerExceptionEvent::event) isEqualTo event
            get(BrokerExceptionEvent::error).isA<IllegalStateException>()
        }
    }

    @Test
    fun `when publish retained event then event should be retained`() {
        val event = TestEvent("It works!")
        subscribe(testScopeRule, testScopeRule.dispatcher, onEvent = listenerSuccess)
        publish(event, retain = true)

        expectThat(getRetained<TestEvent>()) isEqualTo event
    }

    @Test
    fun `when publish retained event then subscribers should receive it`() {
        val event = TestEvent("It works!")
        subscribe(testScopeRule, testScopeRule.dispatcher, onEvent = listenerSuccess)
        publish(event, retain = true)
        subscribe(testScopeRule, testScopeRule.dispatcher, emitRetained = true, onEvent = listenerSuccess)

        coVerify(exactly = 2) { listenerSuccess(event) }
    }

    @Test
    fun `when remove retained event then retained event should be null`() {
        val event = TestEvent("It works!")
        subscribe(testScopeRule, testScopeRule.dispatcher, onEvent = listenerSuccess)
        publish(event, retain = true)
        removeRetained<TestEvent>()

        expectThat(getRetained<TestEvent>()).isNull()
    }

    suspend fun onEventSuccess(event: TestEvent) {
        // Do nothing
    }

    suspend fun onEventThrowError(event: TestEvent) {
        throw error("Test error")
    }

    suspend fun onEventCatchError(event: BrokerExceptionEvent) {
        // Do nothing
    }

    data class TestEvent(val message: String)
    object UnsubscribedEvent
}
