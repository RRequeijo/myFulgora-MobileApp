package com.example.myfulgora.ui.screens.tabs.home

import androidx.lifecycle.ViewModel
import com.example.myfulgora.data.auth.UserManager
import com.example.myfulgora.data.model.BikeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.example.myfulgora.data.remote.FulgoraMqttClient
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BikeState())
    val uiState: StateFlow<BikeState> = _uiState.asStateFlow()

    init {
        // 1. Carrega os dados fixos primeiro (Nome da mota, etc)
        atualizarEcra()

        // 2. CONECTA AO MQTT (isto era o que faltava!)
        FulgoraMqttClient.connect()

        // 3. Fica a ouvir os dados REAIS do MQTT em tempo real
        viewModelScope.launch {
            FulgoraMqttClient.bikeState.collect { mqttState ->
                // Sempre que o MQTT recebe um JSON, a Home atualiza-se automaticamente
                _uiState.update { currentState ->
                    currentState.copy(
                        batteryPercentage = mqttState.batteryPercentage,
                        range = mqttState.range,
                        isOnline = mqttState.isOnline,
                        isCharging = mqttState.isCharging,
                        batteryCycles = mqttState.batteryCycles,
                        batteryTemp = mqttState.batteryTemp,
                        batteryHealth = mqttState.batteryHealth,
                        timeLeft = mqttState.timeLeft
                    )
                }
            }
        }
    }

    fun atualizarEcra() {
        val currentBike = UserManager.getCurrentBike()
        val total = UserManager.currentUser?.bikes?.size ?: 0

        if (currentBike != null) {
            _uiState.value = BikeState(
                bikeName = currentBike.name,
                totalBikes = total,
                isOnline = currentBike.isConnected,
                isLocked = currentBike.isLocked,
                drivingMode = currentBike.drivingMode,

                averageSpeed = currentBike.averageSpeed,
                range = currentBike.batteryRange.toInt(),
                consumption = currentBike.energyConsumption,
                tyreFront = currentBike.tyreFront,
                tyreBack = currentBike.tyreBack,

                batteryPercentage = currentBike.batteryLevel,
                isCharging = currentBike.isCharging,
                timeLeft = "${currentBike.chargingHours}h ${currentBike.chargingMinutes}m",
                batteryHealth = currentBike.batteryHealth,
                batteryTemp = currentBike.batteryTemperature,
                batteryCycles = currentBike.batteryCycles,
                avgConsumption = currentBike.batteryConsumption
            )
        }
    }

    fun motaSeguinte() {
        UserManager.nextBike()
        atualizarEcra()
    }

    fun motaAnterior() {
        UserManager.previousBike()
        atualizarEcra()
    }
}
