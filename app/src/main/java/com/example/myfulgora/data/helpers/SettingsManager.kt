package com.example.myfulgora.data.helpers

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Cria a instância do DataStore associada ao Contexto
val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    companion object {
        val IS_METRIC_KEY = booleanPreferencesKey("is_metric")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val COOLDOWN_ENABLED = booleanPreferencesKey("cooldown_enabled")
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

    // ==========================================
    // MÉTODOS DE BIOMETRIA
    // ==========================================

    val isBiometricEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[BIOMETRIC_ENABLED] ?: false
        }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED] = enabled
        }
    }

    // ==========================================
    // MÉTODOS DE COOLDOWN (START ENGINE)
    // ==========================================

    val isCooldownEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[COOLDOWN_ENABLED] ?: true // Ativo por padrão
        }

    suspend fun setCooldownEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[COOLDOWN_ENABLED] = enabled
        }
    }
}