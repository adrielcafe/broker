package cafe.adriel.broker

import cafe.adriel.broker.util.TestCoroutineScopeRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class BrokerTest {

    @get:Rule
    val testScopeRule = TestCoroutineScopeRule()

    val broker = Broker(testScopeRule.dispatcher)

    val listenerSuccess = mockk<suspend (TestEvent) -> Unit>(relaxed = true)
    val listenerThrowError = mockk<suspend (TestEvent) -> Unit>(relaxed = true)
    val listenerCatchError = mockk<suspend (BrokerExceptionEvent) -> Unit>(relaxed = true)

    @Before
    fun setup() {
        coEvery { listenerThrowError(any()) } throws IllegalStateException("Test error")

        broker.unsubscribe(this)
    }

    @Test
    fun `when publish event with subscriber then deliver it`() {
        val event = TestEvent("It works!")
        broker.subscribe(this, testScopeRule, testScopeRule.dispatcher, onEvent = listenerSuccess)
        broker.publish(event)

        coVerify { listenerSuccess(event) }
    }

    @Test
    fun `when publish event without subscriber then do not deliver it`() {
        broker.subscribe(this, testScopeRule, testScopeRule.dispatcher, onEvent = listenerSuccess)
        broker.publish(UnsubscribedEvent)

        coVerify(inverse = true) { listenerSuccess(any()) }
    }

    @Test
    fun `when subscriber throws exception then publish exception event`() {
        val exceptionEventSlot = slot<BrokerExceptionEvent>()
        val event = TestEvent("Ops!")
        broker.subscribe(this, testScopeRule, testScopeRule.dispatcher, onEvent = listenerThrowError)
        broker.subscribe(this, testScopeRule, testScopeRule.dispatcher, onEvent = listenerCatchError)
        broker.publish(event)

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
        broker.subscribe(this, testScopeRule, testScopeRule.dispatcher, onEvent = listenerSuccess)
        broker.publish(event, retain = true)

        expectThat(broker.getRetained<TestEvent>()) isEqualTo event
    }

    @Test
    fun `when publish retained event then subscribers should receive it`() {
        val event = TestEvent("It works!")
        broker.subscribe(this, testScopeRule, testScopeRule.dispatcher, emitRetained = false, onEvent = listenerSuccess)
        broker.publish(event, retain = true)
        broker.subscribe(this, testScopeRule, testScopeRule.dispatcher, emitRetained = true, onEvent = listenerSuccess)

        coVerify(exactly = 2) { listenerSuccess(event) }
    }

    @Test
    fun `when remove retained event then retained event should be null`() {
        val event = TestEvent("It works!")
        broker.publish(event, retain = true)
        broker.removeRetained<TestEvent>()

        expectThat(broker.getRetained<TestEvent>()).isNull()
    }

    @Test
    fun `when remove not retained event then nothing should happen`() {
        broker.removeRetained<UnsubscribedEvent>()

        expectThat(broker.getRetained<UnsubscribedEvent>()).isNull()
    }

    data class TestEvent(val message: String)
    object UnsubscribedEvent
}
