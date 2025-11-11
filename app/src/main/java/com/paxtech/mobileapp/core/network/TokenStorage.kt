package com.paxtech.mobileapp.core.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("auth_prefs")
class TokenStorage @Inject constructor(@ApplicationContext private val context: Context) {
    private val KEY_TOKEN = stringPreferencesKey("jwt")

    val tokenFlow: Flow<String?> = context.dataStore.data.map { preferences -> preferences[KEY_TOKEN] }

    suspend fun save(token: String) {
        context.dataStore.edit { it[KEY_TOKEN] = token }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(KEY_TOKEN) }
    }


}