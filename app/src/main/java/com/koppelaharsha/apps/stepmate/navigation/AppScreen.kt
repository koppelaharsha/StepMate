package com.koppelaharsha.apps.stepmate.navigation

import androidx.annotation.StringRes
import com.koppelaharsha.apps.stepmate.R

enum class AppScreen(@StringRes val title: Int) {
    Dashboard(title = R.string.app_name),
    PairedDevices(title = R.string.paired_devices)
}
