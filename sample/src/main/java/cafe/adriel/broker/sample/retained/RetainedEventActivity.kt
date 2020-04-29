package cafe.adriel.broker.sample.retained

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cafe.adriel.broker.GlobalBroker
import cafe.adriel.broker.removeRetained
import cafe.adriel.broker.sample.R
import cafe.adriel.broker.sample.SampleEvent
import cafe.adriel.broker.subscribe
import cafe.adriel.broker.unsubscribe
import kotlinx.android.synthetic.main.activity_retained_events.*

class RetainedEventActivity : AppCompatActivity(R.layout.activity_retained_events), GlobalBroker.Subscriber {

    private val viewModel by viewModels<RetainedEventViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Retained Event"
        publishEvent.setOnClickListener {
            viewModel.doSomething()
        }
        removeRetainedEvent.setOnClickListener {
            val message = removeRetained<SampleEvent>()
                ?.let { "Retained event removed" }
                ?: "No retained event found"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        subscribe<SampleEvent>(lifecycleScope, emitRetained = true) { event ->
            Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        unsubscribe()
        super.onStop()
    }
}
