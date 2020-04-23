package cafe.adriel.broker

import cafe.adriel.broker.util.TestCoroutineScopeRule
import io.mockk.coVerify
import io.mockk.spyk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BrokerTest {

    @get:Rule
    val testScopeRule = TestCoroutineScopeRule()

    val broker = spyk(GlobalBroker)
    val listenerSuccess = spyk(::onEventSuccess)
    val listenerThrowError = spyk(::onEventThrowError)
    val listenerCatchError = spyk(::onEventCatchError)

    @Before
    fun setup() {
        broker.unsubscribe(this, testScopeRule)
    }

    @Test
    fun `when publish event then deliver to subscriber`() {
        val event = TestEvent("It works!")
        broker.subscribe(this, testScopeRule, testScopeRule.dispatcher, listenerSuccess)
        broker.publish(event)

        coVerify { listenerSuccess(event) }
    }

    @Test
    fun `when subscriber throws exception then publish exception event`() {
        val event = TestEvent("Ops!")
        broker.subscribe(this, testScopeRule, testScopeRule.dispatcher, listenerThrowError)
        broker.subscribe(this, testScopeRule, testScopeRule.dispatcher, listenerCatchError)
        broker.publish(event)

        coVerify { listenerThrowError(event) }
        coVerify { listenerCatchError(any()) }
    }

    suspend fun onEventSuccess(event: TestEvent) {}

    suspend fun onEventThrowError(event: TestEvent) { throw error("Test error") }

    suspend fun onEventCatchError(event: BrokerExceptionEvent) {}

    data class TestEvent(val message: String)
}
