package com.koppelaharsha.apps.stepmate

import android.content.Context
import android.util.Log
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import javax.inject.Singleton

object Utils {

    private var permissionsGranted: Boolean = false

    fun isPermissionGranted(): Boolean {
        return permissionsGranted
    }

    fun checkBTPermissions(context: Context) {
        Dexter.withContext(context)
            .withPermissions(
                Constants.permissionsList
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    permissionsGranted = report?.areAllPermissionsGranted() == true
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            })
            .check()
    }
}
