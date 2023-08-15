package com.koppelaharsha.apps.stepmate

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.koppelaharsha.apps.stepmate.data.BtBroadcastReceiver
import com.koppelaharsha.apps.stepmate.screens.App
import com.koppelaharsha.apps.stepmate.ui.theme.StepMateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val btBroadcastReceiver: BroadcastReceiver = BtBroadcastReceiver()
    private val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(btBroadcastReceiver, filter)
        setContent {
            StepMateTheme {
                App()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(btBroadcastReceiver)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {

}
