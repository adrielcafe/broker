package cafe.adriel.broker.sample.lifecycle

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cafe.adriel.broker.GlobalBroker
import cafe.adriel.broker.lifecycle.subscribe
import cafe.adriel.broker.sample.R.layout
import cafe.adriel.broker.sample.SampleEvent
import cafe.adriel.broker.subscribe
import kotlinx.android.synthetic.main.activity_broker.*

class LifecycleBrokerActivity : AppCompatActivity(layout.activity_broker), GlobalBroker.Subscriber {

    private val viewModel by viewModels<LifecycleBrokerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        publishEvent.setOnClickListener {
            viewModel.doSomething()
        }

        subscribe<SampleEvent>(this) { event ->
            Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
        }
    }
}
