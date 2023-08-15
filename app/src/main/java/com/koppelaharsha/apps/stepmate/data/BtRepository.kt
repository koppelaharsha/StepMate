package com.koppelaharsha.apps.stepmate.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import com.koppelaharsha.apps.stepmate.Constants
import com.koppelaharsha.apps.stepmate.Utils
import com.koppelaharsha.apps.stepmate.model.BtDevice
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import kotlin.random.Random

sealed class BtConnStatus {
    object connecting : BtConnStatus()
    object connected : BtConnStatus()
    data class disconnected(val error: String = "") : BtConnStatus()
}

interface BtRepository {
    fun getBtStatus(): StateFlow<Boolean>
    fun setBtStatus(turnedOn: Boolean)
    fun loadPairedBtDevices()
    fun getPairedBtDevices(): StateFlow<List<BtDevice>>
    fun selectDevice(sbtDevice: BtDevice)
    fun getSelectedBtDevice(): StateFlow<BtDevice?>
    fun getBtConnStatus(): StateFlow<BtConnStatus>
    suspend fun connectDevice()
    fun disconnectDevice(msg: String = "")
    fun getData(): Flow<String>
}

class BtRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository
) : BtRepository {

    private val btAdapter = context.getSystemService(BluetoothManager::class.java).adapter

    private val _btOnStatus: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _btConnStatus: MutableStateFlow<BtConnStatus> =
        MutableStateFlow(BtConnStatus.disconnected())

    private var _pairedDevicesList: List<BluetoothDevice> = emptyList()

    private val _pairedBtDevicesList = MutableStateFlow(emptyList<BtDevice>())

    private var _selectedDevice: BluetoothDevice? = null

    private val _selectedBtDevice: MutableStateFlow<BtDevice?> = MutableStateFlow(null)

    override fun getBtStatus(): StateFlow<Boolean> {
        return _btOnStatus.asStateFlow()
    }

    override fun setBtStatus(turnedOn: Boolean) {
        disconnectDevice()
        _btOnStatus.value = turnedOn
    }

    init {
        if (btAdapter.isEnabled) {
            setBtStatus(true)
        }
        loadPairedBtDevices()
        CoroutineScope(Dispatchers.Default).launch {
            userPreferencesRepository.getDevice.collect { stDevice ->
                if (stDevice != null && stDevice != _selectedBtDevice.value) {
                    selectDevice(stDevice)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun loadPairedBtDevices() {
        Utils.checkBTPermissions(context)
        if (Utils.isPermissionGranted()) {
            val newPairedDevicesList = btAdapter.bondedDevices.toList()
            if (newPairedDevicesList != _pairedDevicesList) {
                _pairedDevicesList = newPairedDevicesList
                _pairedBtDevicesList.value =
                    _pairedDevicesList.map { BtDevice(it.name, it.address) }
            }
        }
    }

    override fun getPairedBtDevices(): StateFlow<List<BtDevice>> {
        return _pairedBtDevicesList.asStateFlow()
    }

    override fun selectDevice(sbtDevice: BtDevice) {
        _selectedDevice = _pairedDevicesList.firstOrNull { it.address == sbtDevice.hwAddress }
        if (_selectedDevice != null) {
            _selectedBtDevice.value = sbtDevice
            CoroutineScope(Dispatchers.Default).launch {
                userPreferencesRepository.saveDevice(sbtDevice)
            }
        }
    }

    override fun getSelectedBtDevice(): StateFlow<BtDevice?> {
        return _selectedBtDevice.asStateFlow()
    }

    override fun getBtConnStatus(): StateFlow<BtConnStatus> {
        return _btConnStatus.asStateFlow()
    }

    private var socket: BluetoothSocket? = null

    @SuppressLint("MissingPermission")
    override suspend fun connectDevice() {
        withContext(Dispatchers.IO) {
            disconnectDevice()
            if (btAdapter.isEnabled) {
                _btConnStatus.value = BtConnStatus.connecting
                _selectedDevice?.let {
                    socket = it.createRfcommSocketToServiceRecord(Constants.SPP_UUID)
                }
                try {
                    socket?.connect()
                    if (socket?.isConnected == true) {
                        _btConnStatus.value = BtConnStatus.connected
                    } else {
                        disconnectDevice("Could Not Connect")
                    }
                } catch (e: IOException) {
                    disconnectDevice(e.localizedMessage ?: "Could Not Connect")
                }
            } else {
                _btOnStatus.value = false
            }
        }
    }

    override fun disconnectDevice(msg: String) {
        try {
            socket?.close()
            if (msg.isEmpty()) {
                _btConnStatus.value = BtConnStatus.disconnected()
            } else {
                _btConnStatus.value = BtConnStatus.disconnected(msg)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun getData(): Flow<String> {
        return flow {
            while (_btConnStatus.value == BtConnStatus.connected) {
                val data = withContext(Dispatchers.IO) {
                    try {
                        val buffer = ByteArray(512)
                        val bytes = socket?.inputStream?.read(buffer)
                        socket?.outputStream?.write(1)
                        String(buffer, 0, bytes ?: 0)
                    } catch (e: IOException) {
                        disconnectDevice()
                        "0"
                    }
                }
                emit(data)
                delay(Constants.BT_WAIT_TIME_IN_SECS*1000L)
            }
        }
    }

}
