package cafe.adriel.broker.lifecycle

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import cafe.adriel.broker.BrokerSubscriber
import cafe.adriel.broker.GlobalBroker
import cafe.adriel.broker.subscribe
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

inline fun <reified T : Any> BrokerSubscriber.subscribe(
    owner: LifecycleOwner,
    dispatcher: CoroutineContext = Dispatchers.Main,
    emitRetained: Boolean = false,
    noinline onEvent: suspend (T) -> Unit
) =
    subscribe(owner, owner, dispatcher, emitRetained, onEvent)

inline fun <reified T : Any> BrokerSubscriber.subscribe(
    subscriber: Any,
    owner: LifecycleOwner,
    dispatcher: CoroutineContext = Dispatchers.Main,
    emitRetained: Boolean = false,
    noinline onEvent: suspend (T) -> Unit
) =
    owner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Event) {
            when (event) {
                ON_START -> subscribe(subscriber, owner.lifecycleScope, dispatcher, emitRetained, onEvent)
                ON_STOP -> unsubscribe(subscriber)
            }
        }
    })

inline fun <reified T : Any> GlobalBroker.Subscriber.subscribe(
    owner: LifecycleOwner,
    dispatcher: CoroutineContext = Dispatchers.Main,
    emitRetained: Boolean = false,
    noinline onEvent: suspend (T) -> Unit
) =
    GlobalBroker.subscribe(this, owner, dispatcher, emitRetained, onEvent)
