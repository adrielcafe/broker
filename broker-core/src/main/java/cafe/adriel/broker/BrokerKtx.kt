package cafe.adriel.broker

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

inline fun <reified T : Any> BrokerSubscriber.subscribe(
    subscriber: Any,
    scope: CoroutineScope,
    dispatcher: CoroutineContext = Dispatchers.Main,
    emitRetained: Boolean = false,
    noinline onEvent: suspend (T) -> Unit
) =
    subscribe(subscriber, T::class, scope, dispatcher, emitRetained, onEvent)

inline fun <reified T : Any> BrokerSubscriber.getRetained(): T? =
    getRetained(T::class)

inline fun <reified T : Any> BrokerSubscriber.removeRetained(): T? =
    removeRetained(T::class)

fun GlobalBroker.Publisher.publish(event: Any, retain: Boolean = false) =
    GlobalBroker.publish(event, retain)

inline fun <reified T : Any> GlobalBroker.Subscriber.subscribe(
    scope: CoroutineScope,
    dispatcher: CoroutineContext = Dispatchers.Main,
    emitRetained: Boolean = false,
    noinline onEvent: suspend (T) -> Unit
) =
    GlobalBroker.subscribe(this, scope, dispatcher, emitRetained, onEvent)

fun GlobalBroker.Subscriber.unsubscribe() =
    GlobalBroker.unsubscribe(this)

inline fun <reified T : Any> GlobalBroker.Subscriber.getRetained(): T? =
    GlobalBroker.getRetained<T>()

inline fun <reified T : Any> GlobalBroker.Subscriber.removeRetained(): T? =
    GlobalBroker.removeRetained<T>()
