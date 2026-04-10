package com.example.myfulgora.data.helpers

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. Cria a instância do DataStore associada ao Contexto (Isto fica fora da classe, está correto)
val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    // 2. Colocamos as Chaves todas arrumadinhas aqui dentro
    companion object {
        val IS_METRIC_KEY = booleanPreferencesKey("is_metric")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    // ==========================================
    // MÉTODOS DO ONBOARDING
    // ==========================================

    val isOnboardingCompletedFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }

    suspend fun guardarOnboardingCompleto(completo: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completo
        }
    }

    // ==========================================
    // MÉTODOS DO SISTEMA MÉTRICO / IMPERIAL
    // ==========================================

    val isMetricFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_METRIC_KEY] ?: true
        }

    suspend fun saveIsMetric(isMetric: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_METRIC_KEY] = isMetric
        }
    }
}