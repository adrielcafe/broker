package cafe.adriel.broker.sample.local

import androidx.lifecycle.ViewModel
import cafe.adriel.broker.BrokerPublisher
import cafe.adriel.broker.publish
import cafe.adriel.broker.sample.SampleEvent

class LocalBrokerViewModel(val broker: BrokerPublisher) : ViewModel() {

    fun doSomething() {
        broker.publish(SampleEvent("Hello!"))
    }
}
