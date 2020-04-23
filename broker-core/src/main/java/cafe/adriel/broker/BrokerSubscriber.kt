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
        onEvent: suspend (T) -> Unit
    )

    fun unsubscribe(subscriber: Any, scope: CoroutineScope)
}
