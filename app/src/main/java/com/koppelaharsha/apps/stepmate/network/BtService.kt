package com.koppelaharsha.apps.stepmate.network

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.koppelaharsha.apps.stepmate.Constants
import com.koppelaharsha.apps.stepmate.data.BtRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothService : Service() {

    @Inject
    lateinit var btRepository: BtRepository

    private val btAdapter = getSystemService(BluetoothManager::class.java).adapter

    private lateinit var bluetoothSocket: BluetoothSocket

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

//        if (btAdapter.isEnabled) {
//            val pairedDevices = btAdapter.bondedDevices
//            for (device in pairedDevices) {
//                if (device.name == "ESP32_BT_Classic") {
//                    bluetoothDevice = device
//                    break
//                }
//            }
//            if (bluetoothDevice != null) {
//                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(Constants.SPP_UUID)
//                bluetoothSocket.connect()
//                val inputStream = bluetoothSocket.inputStream
//                while (true) {
//                    val data = inputStream.read()
//                    val floatData = data.toFloat()
//                    sendDataToViewModel(floatData)
//                    btViewModel.
//                }
//            }
//        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothSocket.close()
    }

//    private fun sendDataToViewModel(data: Float) {
//        val intent = Intent(DATA_RECEIVED_ACTION)
//        intent.putExtra(DATA_KEY, data)
//        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
//        btViewModel.handleDataReceived(data)
//    }

//    companion object {
//        const val DATA_RECEIVED_ACTION = "com.example.bluetooth.DATA_RECEIVED"
//        const val DATA_KEY = "data"
//        val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
//    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

}



