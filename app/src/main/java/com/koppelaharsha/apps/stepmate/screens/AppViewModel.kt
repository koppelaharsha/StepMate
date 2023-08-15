package com.koppelaharsha.apps.stepmate.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koppelaharsha.apps.stepmate.Constants
import com.koppelaharsha.apps.stepmate.data.BtConnStatus
import com.koppelaharsha.apps.stepmate.data.BtRepository
import com.koppelaharsha.apps.stepmate.model.BtData
import com.koppelaharsha.apps.stepmate.model.BtDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val btRepository: BtRepository) : ViewModel() {

    val btStatus: StateFlow<Boolean> = btRepository.getBtStatus()

    val btDevices: StateFlow<List<BtDevice>> = btRepository.getPairedBtDevices()

    val btDevice: StateFlow<BtDevice?> = btRepository.getSelectedBtDevice()

    val btConnStatus: StateFlow<BtConnStatus> = btRepository.getBtConnStatus()

    fun setBtDevice(device: BtDevice) {
        btRepository.selectDevice(device)
        connectDevice()
    }

    private var _remData: String = ""

    private val _dataState = MutableStateFlow(BtData())
    val dataState: StateFlow<BtData> = _dataState.asStateFlow()

    fun loadPairedDevices() {
        btRepository.loadPairedBtDevices()
    }

    fun connectDevice() {
        viewModelScope.launch {
            btRepository.connectDevice()
            btRepository.getData().collect {
                parseData(it)
            }
        }
    }

    private fun parseData(data: String = "") {
        val pData = "${_remData}${data}"
        val segs = pData.split("1+".toRegex()).toMutableList()
        if (segs[0].isBlank()) {
            segs.removeAt(0)
        }
        _remData = segs.removeLast()
        if (_remData.isNotBlank()) {
            _remData = "0"
        }
        if (segs.size % Constants.NO_OF_1s_IN_STEP != 0) {
            segs.removeLast()
            _remData = if (_remData.isEmpty()) {
                "01"
            } else {
                "010"
            }
        }
        var cSteps = segs.size / Constants.NO_OF_1s_IN_STEP
        cSteps = minOf(cSteps, Constants.MAX_STEP_RATE)
        Log.d("CSEGS", segs.toString())
        Log.d("CSTEPS", cSteps.toString())
        _dataState.update {
            val dp = it.dataPoints.toMutableList()
            dp.add(cSteps)
            if(dp.size > Constants.MAX_DATA_POINTS){
                dp.removeAt(0)
            }
            BtData(
                it.stepCount + cSteps,
                dp,
                when (cSteps) {
                    0 -> "IDLE"
                    in Constants.WALK_RANGE -> "WALKING"
                    in Constants.JOG_RANGE -> "JOGGING"
                    else -> "SPRINTING"
                }
            )
        }
    }

    fun resetData() {
        _remData = ""
        _dataState.value = BtData()
    }

    fun disconnectDevice() {
        viewModelScope.launch {
            btRepository.disconnectDevice()
        }
    }

    override fun onCleared() {
        super.onCleared()
        btRepository.disconnectDevice()
    }
}
