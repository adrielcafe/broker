package cafe.adriel.broker.sample.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cafe.adriel.broker.BrokerPublisher

class LocalBrokerViewModelFactory(private val broker: BrokerPublisher) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T = LocalBrokerViewModel(broker) as T
}
