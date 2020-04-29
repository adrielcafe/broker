package cafe.adriel.broker.sample.lifecycle

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cafe.adriel.broker.GlobalBroker
import cafe.adriel.broker.lifecycle.subscribe
import cafe.adriel.broker.sample.R
import cafe.adriel.broker.sample.SampleEvent
import kotlinx.android.synthetic.main.activity_broker.*

class LifecycleBrokerActivity : AppCompatActivity(R.layout.activity_broker), GlobalBroker.Subscriber {

    private val viewModel by viewModels<LifecycleBrokerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Lifecycle-aware Broker"
        publishEvent.setOnClickListener {
            viewModel.doSomething()
        }

        subscribe<SampleEvent>(this) { event ->
            Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
        }
    }
}
