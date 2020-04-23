package cafe.adriel.broker.sample.lifecycle

import androidx.lifecycle.ViewModel
import cafe.adriel.broker.GlobalBroker
import cafe.adriel.broker.publish
import cafe.adriel.broker.sample.SampleEvent

class LifecycleBrokerViewModel : ViewModel(), GlobalBroker.Publisher {

    fun doSomething() {
        publish(SampleEvent("Hello!"))
    }
}
