package cafe.adriel.broker

import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

interface BrokerSubscriber {

    fun <T : Any> subscribe(
        subscriber: Any,
        eventClass: KClass<T>,
        scope: CoroutineScope,
        eventDispatcher: CoroutineContext = Dispatchers.Main,
        emitRetained: Boolean = false,
        onEvent: suspend (T) -> Unit
    )

    fun unsubscribe(subscriber: Any)

    fun <T : Any> getRetained(eventClass: KClass<T>): T?

    fun <T : Any> removeRetained(eventClass: KClass<T>): T?
}
