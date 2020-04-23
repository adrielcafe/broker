package cafe.adriel.broker.sample.local

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cafe.adriel.broker.Broker
import cafe.adriel.broker.sample.R.layout
import cafe.adriel.broker.sample.SampleEvent
import cafe.adriel.broker.subscribe
import cafe.adriel.broker.unsubscribe
import kotlinx.android.synthetic.main.activity_broker.*

class LocalBrokerActivity : AppCompatActivity(layout.activity_broker) {

    private val broker by lazy { Broker() }

    private val viewModel by viewModels<LocalBrokerViewModel> {
        LocalBrokerViewModelFactory(broker)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        publishEvent.setOnClickListener {
            viewModel.doSomething()
        }
    }

    override fun onStart() {
        super.onStart()
        broker.subscribe<SampleEvent>(this, lifecycleScope) { event ->
            Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        broker.unsubscribe(this, lifecycleScope)
        super.onStop()
    }
}
