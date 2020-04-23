package cafe.adriel.broker.sample.global

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cafe.adriel.broker.GlobalBroker
import cafe.adriel.broker.sample.R
import cafe.adriel.broker.sample.SampleEvent
import cafe.adriel.broker.subscribe
import cafe.adriel.broker.unsubscribe
import kotlinx.android.synthetic.main.activity_broker.*

class GlobalBrokerActivity : AppCompatActivity(R.layout.activity_broker), GlobalBroker.Subscriber {

    private val viewModel by viewModels<GlobalBrokerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Global Broker"
        publishEvent.setOnClickListener {
            viewModel.doSomething()
        }
    }

    override fun onStart() {
        super.onStart()
        subscribe<SampleEvent>(lifecycleScope) { event ->
            Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        unsubscribe(lifecycleScope)
        super.onStop()
    }
}
