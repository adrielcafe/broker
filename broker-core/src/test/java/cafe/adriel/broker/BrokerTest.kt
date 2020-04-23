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

class BrokerTest : GlobalBroker.Publisher, GlobalBroker.Subscriber {

    @get:Rule
    val testScopeRule = TestCoroutineScopeRule()

    val listenerSuccess = spyk(::onEventSuccess)
    val listenerThrowError = spyk(::onEventThrowError)
    val listenerCatchError = spyk(::onEventCatchError)

    @Before
    fun setup() {
        unsubscribe(testScopeRule)
    }

    @Test
    fun `when publish event with subscriber then deliver it`() {
        val event = TestEvent("It works!")
        subscribe(testScopeRule, testScopeRule.dispatcher, listenerSuccess)
        publish(event)

        coVerify { listenerSuccess(event) }
    }

    @Test
    fun `when publish event without subscriber then do not deliver it`() {
        val event = TestEvent("It works!")
        publish(event)

        coVerify(inverse = true) { listenerSuccess(event) }
    }

    @Test
    fun `when subscriber throws exception then publish exception event`() {
        val exceptionEventSlot = slot<BrokerExceptionEvent>()
        val event = TestEvent("Ops!")
        subscribe(testScopeRule, testScopeRule.dispatcher, listenerThrowError)
        subscribe(testScopeRule, testScopeRule.dispatcher, listenerCatchError)
        publish(event)

        coVerify { listenerThrowError(event) }
        coVerify { listenerCatchError(capture(exceptionEventSlot)) }

        expectThat(exceptionEventSlot.captured) {
            get(BrokerExceptionEvent::subscriber) isEqualTo this@BrokerTest
            get(BrokerExceptionEvent::event) isEqualTo event
            get(BrokerExceptionEvent::error).isA<IllegalStateException>()
        }
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
}
