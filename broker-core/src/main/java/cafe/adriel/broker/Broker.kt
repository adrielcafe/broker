package cafe.adriel.broker

import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

open class Broker(
    private val dispatcher: CoroutineContext = Dispatchers.Default
) : BrokerPublisher, BrokerSubscriber {

    private val eventChannel by lazy { BroadcastChannel<Any>(Channel.BUFFERED) }
    private val subscriberJobs by lazy { mutableMapOf<Any, Set<Job>>() }
    private val subscriberMutex by lazy { Mutex() }

    override fun publish(event: Any) {
        eventChannel.sendBlocking(event)
    }

    override fun <T : Any> subscribe(
        subscriber: Any,
        eventClass: KClass<T>,
        scope: CoroutineScope,
        eventDispatcher: CoroutineContext,
        onEvent: suspend (T) -> Unit
    ) {
        val newJob = eventChannel
            .asFlow()
            .filter { event -> event::class == eventClass }
            .flatMapConcat { event ->
                flow { emit(onEvent(event as T)) }
                    .catch { error -> publish(BrokerExceptionEvent(subscriber, event, error)) }
                    .flowOn(eventDispatcher)
            }
            .flowOn(dispatcher)
            .launchIn(scope)

        withMutex(scope) {
            subscriberJobs[subscriber] = getJobs(subscriber) + newJob
        }
    }

    override fun unsubscribe(subscriber: Any, scope: CoroutineScope) {
        withMutex(scope) {
            getJobs(subscriber).forEach { job -> job.cancel() }
            subscriberJobs.remove(subscriber)
        }
    }

    private fun getJobs(subscriber: Any): Set<Job> =
        subscriberJobs[subscriber] ?: emptySet()

    private fun withMutex(scope: CoroutineScope, action: () -> Unit) {
        scope.launch(dispatcher) {
            subscriberMutex.withLock(action = action)
        }
    }
}
