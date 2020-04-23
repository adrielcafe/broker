package cafe.adriel.broker

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

inline fun <reified T : Any> BrokerSubscriber.subscribe(
    subscriber: Any,
    scope: CoroutineScope,
    dispatcher: CoroutineContext = Dispatchers.Main,
    noinline onEvent: suspend (T) -> Unit
) =
    subscribe(subscriber, T::class, scope, dispatcher, onEvent)

fun GlobalBroker.Publisher.publish(event: Any) =
    GlobalBroker.publish(event)

inline fun <reified T : Any> GlobalBroker.Subscriber.subscribe(
    scope: CoroutineScope,
    dispatcher: CoroutineContext = Dispatchers.Main,
    noinline onEvent: suspend (T) -> Unit
) =
    GlobalBroker.subscribe(this, scope, dispatcher, onEvent)

fun GlobalBroker.Subscriber.unsubscribe(scope: CoroutineScope) =
    GlobalBroker.unsubscribe(this, scope)
