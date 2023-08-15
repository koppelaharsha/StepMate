package com.koppelaharsha.apps.stepmate.screens

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.component2
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.koppelaharsha.apps.stepmate.data.BtConnStatus

@Composable
fun DashboardScreen(viewModel: AppViewModel, viewPairedDevices: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        BtConnStatusBox(viewModel)
        DataBox(viewModel, viewPairedDevices)
    }
}

@Composable
fun BtConnStatusBox(viewModel: AppViewModel) {
    val btDevice = viewModel.btDevice.collectAsState().value
    Card(modifier = Modifier.fillMaxWidth(), shape = RectangleShape) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = if (btDevice == null) {
                "No Device Selected"
            } else {
                when (val btConnStatus: BtConnStatus =
                    viewModel.btConnStatus.collectAsState().value) {
                    BtConnStatus.connected -> "Connected to: ${btDevice.name}"
                    BtConnStatus.connecting -> "Connecting to: ${btDevice.name}"
                    is BtConnStatus.disconnected -> {
                        if (btConnStatus.error.isNotEmpty()) {
                            "Could not connect to: ${btDevice.name}"
                        } else {
                            "Not Connected to: ${btDevice.name}"
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun DataBox(viewModel: AppViewModel, viewPairedDevices: () -> Unit) {
    val btStatus = viewModel.btStatus.collectAsState().value
    if (!btStatus) {
        val openBluetoothSettings = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {
            if(it.resultCode == Activity.RESULT_OK){
                Log.d("BT_ENABLE_REQUEST","ok")
            }
        }
        NoData("Turn On Bluetooth") {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            openBluetoothSettings.launch(intent)
        }
    } else {
        val btDevice = viewModel.btDevice.collectAsState().value
        if (btDevice == null) {
            NoData("Connect to a paired Bluetooth Device") {
                viewPairedDevices()
            }
        } else {
            when (val btConnStatus: BtConnStatus = viewModel.btConnStatus.collectAsState().value) {
                BtConnStatus.connected -> {
                    StepData(viewModel)
                }

                BtConnStatus.connecting -> {
                    NoData("Connecting to ${btDevice.name}")
                }

                is BtConnStatus.disconnected -> {
                    if (btConnStatus.error.isNotEmpty()) {
                        NoData("Retry connecting to ${btDevice.name}") {
                            viewModel.connectDevice()
                        }
                    } else {
                        NoData("Connect to ${btDevice.name}") {
                            viewModel.connectDevice()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepData(viewModel: AppViewModel) {
    val dataState = viewModel.dataState.collectAsState().value
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Steps taken: ${dataState.stepCount}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Current Activity: ${dataState.currActivity}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Graph(_points = dataState.dataPoints)
        }
        Button(onClick = { viewModel.resetData() }) {
            Text(text = "Reset Data")
        }
    }
}

@Composable
fun NoData(btnText: String, btnAction: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (btnAction == null)
            OutlinedButton(onClick = {}) {
                Text(btnText)
            } else {
            Button(onClick = { btnAction() }) {
                Text(text = btnText)
            }
        }
    }
}

/*

@Composable
fun DS2(viewModel: AppViewModel, viewPairedDevices: () -> Unit) {
    val btStatus = viewModel.btStatus.collectAsState().value
    if (!btStatus) {
        NoData("Turn On Bluetooth") {
            Log.d("BT_ON_BTN", "BT_ON_BTN")
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        }
    } else {
        val btDevice = viewModel.btDevice.collectAsState().value
        if (btDevice == null) {
            NoData("Connect to a paired Bluetooth Device") {
                viewPairedDevices()
            }
        } else {
            when (val btConnStatus: BtConnStatus = viewModel.btConnStatus.collectAsState().value) {

                BtConnStatus.connected -> {
                    DisplayData(viewModel)
                }

                BtConnStatus.connecting -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RectangleShape) {
                            Text(
                                "Connecting to: ${btDevice.name}",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                is BtConnStatus.disconnected -> {
                    if (btConnStatus.error.isNotEmpty()) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RectangleShape) {
                                Text(
                                    "Could not connect to: ${btDevice.name}",
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                        NoData("Retry connecting to ${btDevice.name}") {
                            viewModel.connectDevice()
                        }
                    } else {
                        NoData("Connect to ${btDevice.name}") {
                            viewModel.connectDevice()
                        }
                    }
                }

            }
        }
    }
}


@Composable
fun DisplayData(viewModel: AppViewModel) {
    val device = viewModel.btDevice.collectAsState().value
    val stepCount = viewModel.stepCount.collectAsState().value
    val currActivity = viewModel.currActivity.collectAsState().value
    val dataPoints = viewModel.dataPoints.collectAsState().value
    Column(modifier = Modifier.fillMaxSize()) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RectangleShape) {
            Text("Connected to: ${device?.name}", modifier = Modifier.padding(8.dp))
        }
        Text(
            "Steps taken: $stepCount",
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Current Activity: $currActivity",
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Graph(_points = dataPoints)
        }
        Button(onClick = { viewModel.resetData() }) {
            Text(text = "Reset Data")
        }
    }
}
*/
