package com.example.myfulgora.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfulgora.data.auth.UserManager
import com.example.myfulgora.data.remote.FulgoraMqttClient
import com.example.myfulgora.data.model.BikeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.myfulgora.data.helpers.NotificationManager // Importa o teu Cérebro!

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val bikeState: BikeState) : HomeUiState()
}

class MotaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private var alreadyNotifiedBattery = false

    init {
        conectarMqtt()

        // 1. Observar mudança de mota no UserManager
        viewModelScope.launch {
            UserManager.activeBikeIndexFlow.collectLatest {
                atualizarEstadoLocal()
            }
        }

        // 2. Escutar dados do MQTT
        viewModelScope.launch {
            FulgoraMqttClient.bikeState.collectLatest { mqttState ->
                val currentBike = UserManager.getCurrentBike()
                val total = UserManager.currentUser?.bikes?.size ?: 0

                // Estado base atual (normalmente vindo do JSON local)
                val base = (_uiState.value as? HomeUiState.Success)?.bikeState ?: BikeState()

                // Fundimos os dados do MQTT (dinâmicos) com o estado base (JSON/local)
                _uiState.value = HomeUiState.Success(
                    base.copy(
                        bikeName = currentBike?.name ?: base.bikeName,
                        totalBikes = total,
                        batteryPercentage = mqttState.batteryPercentage,
                        range = mqttState.range,
                        isOnline = mqttState.isOnline,
                        isCharging = mqttState.isCharging
                    )
                )
            }
        }
    }

    private fun atualizarEstadoLocal() {
        val currentBike = UserManager.getCurrentBike()
        val total = UserManager.currentUser?.bikes?.size ?: 0

        val newState = if (currentBike != null) {
            // Mapeia TODOS os campos relevantes do MockBike para o BikeState da UI
            BikeState(
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
        } else {
            BikeState(
                bikeName = "No Bike",
                totalBikes = total
            )
        }

        _uiState.value = HomeUiState.Success(newState)

        // --- GATILHO DAS NOTIFICAÇÕES (Bateria) ---
        val bateriaAtual = mqttState.batteryPercentage
        if (bateriaAtual <= 20 && !alreadyNotifiedBattery) {
            NotificationManager.addNotification(
                title = "Bateria Fraca",
                message = "A bateria desceu para ${bateriaAtual}%. Planeia o carregamento."
            )
            alreadyNotifiedBattery = true
        } else if (bateriaAtual > 20) {
            alreadyNotifiedBattery = false // Faz reset se a mota for carregada
        }
    }

    private fun conectarMqtt() {
        FulgoraMqttClient.connect()
    }

    // Funções chamadas pelas setas na HomeScreen
    fun motaSeguinte() {
        UserManager.nextBike()
    }

    fun motaAnterior() {
        UserManager.previousBike()
    }
}
