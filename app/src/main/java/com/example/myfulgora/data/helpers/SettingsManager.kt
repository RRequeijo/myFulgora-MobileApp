package com.example.myfulgora.data.helpers


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. Cria o ficheiro de memória "settings" no telemóvel
val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    // 2. Define a "Chave" onde vamos guardar a escolha (Metric = true, Imperial = false)
    companion object {
        val IS_METRIC_KEY = booleanPreferencesKey("is_metric")
    }

    // 3. Lê o valor continuamente (Flow). Se não existir nada guardado, assume "true" (KM) por defeito.
    val isMetricFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_METRIC_KEY] ?: true
        }

    // 4. Guarda a nova escolha quando o utilizador clica no botão
    suspend fun saveIsMetric(isMetric: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_METRIC_KEY] = isMetric
        }
    }
}