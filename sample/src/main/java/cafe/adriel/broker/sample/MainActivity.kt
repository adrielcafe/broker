package cafe.adriel.broker.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cafe.adriel.broker.sample.global.GlobalBrokerActivity
import cafe.adriel.broker.sample.lifecycle.LifecycleBrokerActivity
import cafe.adriel.broker.sample.local.LocalBrokerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openGlobalExample.setOnClickListener {
            start<GlobalBrokerActivity>()
        }
        openLocalExample.setOnClickListener {
            start<LocalBrokerActivity>()
        }
        openLifecycleExample.setOnClickListener {
            start<LifecycleBrokerActivity>()
        }
    }

    private inline fun <reified T : Activity> start() {
        startActivity(Intent(this, T::class.java))
    }
}
