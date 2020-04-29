package cafe.adriel.broker.sample.retained

import androidx.lifecycle.ViewModel
import cafe.adriel.broker.GlobalBroker
import cafe.adriel.broker.publish
import cafe.adriel.broker.sample.SampleEvent

class RetainedEventViewModel : ViewModel(), GlobalBroker.Publisher {

    fun doSomething() {
        publish(SampleEvent("Hello from Retained Event!"), retain = true)
    }
}
