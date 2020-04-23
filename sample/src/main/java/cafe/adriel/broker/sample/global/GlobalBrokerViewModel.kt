package cafe.adriel.broker.sample.global

import androidx.lifecycle.ViewModel
import cafe.adriel.broker.GlobalBroker
import cafe.adriel.broker.publish
import cafe.adriel.broker.sample.SampleEvent

class GlobalBrokerViewModel : ViewModel(), GlobalBroker.Publisher {

    fun doSomething() {
        publish(SampleEvent("Hello!"))
    }
}
