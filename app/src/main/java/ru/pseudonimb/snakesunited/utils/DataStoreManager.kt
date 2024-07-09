package ru.pseudonimb.snakesunited.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

data class SettingsData(
    var sharedHighScore: Int = 0
)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data_storage")

class DataStoreManager(val context: Context) {

    suspend fun saveSettings(dataSettings: SettingsData) {
        context.dataStore.edit {pref ->
            pref[intPreferencesKey("high_score")] = dataSettings.sharedHighScore
        }
    }

    fun getSettings() = context.dataStore.data.map {pref ->
        return@map SettingsData(
            pref[intPreferencesKey("high_score")] ?: 0
        )
    }
}