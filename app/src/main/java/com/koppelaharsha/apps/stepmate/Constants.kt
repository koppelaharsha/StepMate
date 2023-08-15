package com.koppelaharsha.apps.stepmate

import android.Manifest
import android.os.Build
import java.util.UUID

object Constants {
    val permissionsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )
    }
    val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    val BT_DEVICE_STORED = "BT_DEVICE_STORED"
    val BT_WAIT_TIME_IN_SECS = 1
    val NO_OF_1s_IN_STEP = 1
    val MAX_DATA_POINTS = 15
    val MAX_STEP_RATE = 10

    val WALK_RANGE = 1 until 3
    val JOG_RANGE = 3 until 4

}
