package com.koppelaharsha.apps.stepmate.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.koppelaharsha.apps.stepmate.Constants
import com.koppelaharsha.apps.stepmate.model.BtDevice
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface UserPreferencesRepository {
    val getDevice: Flow<BtDevice?>
    suspend fun saveDevice(btDevice: BtDevice)
}

private val Context.dataStore by preferencesDataStore(Constants.BT_DEVICE_STORED)

class UserPreferencesRepositoryImpl @Inject constructor(@ApplicationContext context: Context): UserPreferencesRepository {

    private val dataStore = context.dataStore

    private companion object {
        val BT_DEVICE_NAME = stringPreferencesKey("bt_device_name")
        val BT_DEVICE_ADDRESS = stringPreferencesKey("bt_device_address")
    }

    override val getDevice: Flow<BtDevice?> = dataStore.data.map { preferences ->
        if (preferences[BT_DEVICE_NAME].isNullOrEmpty() || preferences[BT_DEVICE_ADDRESS].isNullOrEmpty()) {
            null
        } else {
            BtDevice(preferences[BT_DEVICE_NAME]!!, preferences[BT_DEVICE_ADDRESS]!!)
        }
    }

    override suspend fun saveDevice(btDevice: BtDevice) {
        dataStore.edit { preferences ->
            preferences[BT_DEVICE_NAME] = btDevice.name
            preferences[BT_DEVICE_ADDRESS] = btDevice.hwAddress
        }
    }

}
