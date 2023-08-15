package com.koppelaharsha.apps.stepmate.data

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@AndroidEntryPoint
class BtBroadcastReceiver: BroadcastReceiver() {

    @Inject
    lateinit var btRepository: BtRepository

    private var _btON: Boolean = false

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                BluetoothAdapter.STATE_OFF -> {
                    _btON = false
                }
                BluetoothAdapter.STATE_TURNING_OFF -> {
                    _btON = false
                }
                BluetoothAdapter.STATE_ON -> {
                    _btON = true
                }
                BluetoothAdapter.STATE_TURNING_ON -> {
                    _btON = false
                }
            }
            btRepository.setBtStatus(_btON)
        }
    }
}
