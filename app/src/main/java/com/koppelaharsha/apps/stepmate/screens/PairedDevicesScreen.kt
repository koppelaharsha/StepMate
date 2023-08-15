package com.koppelaharsha.apps.stepmate.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.koppelaharsha.apps.stepmate.model.BtDevice

@Composable
fun BluetoothDevicesList(
    viewModel: AppViewModel,
    deviceSelected: (BtDevice) -> Unit
) {
    viewModel.loadPairedDevices()
    val pairedDevices = viewModel.btDevices.collectAsState().value
    val deviceListState = rememberLazyListState()
    LazyColumn(
        state = deviceListState,
        modifier = Modifier.padding(0.dp, 4.dp)
    ) {
        items(pairedDevices) { device ->
            BtDeviceItem(device) {
                deviceSelected(device)
            }
        }
    }
}

@Composable
fun BtDeviceItem(device: BtDevice, clicked: () -> Unit) {
    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .padding(4.dp, 2.dp)
            .clickable { clicked() }
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))) {
        Column(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = device.name ?: "",
                modifier = Modifier.padding(4.dp)
            )
            /*
            Text(
                text = device.hwAddress ?: "",
                modifier = Modifier.padding(4.dp)
            )

             */
        }
    }
}
