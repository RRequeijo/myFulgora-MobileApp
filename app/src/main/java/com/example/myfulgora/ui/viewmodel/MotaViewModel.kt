package com.example.myfulgora.ui.viewmodel

//MotaViewModel (O Chefe de Sala): É o cérebro. Ele vai à cozinha (gRPC/Servidor Python), pega na comida (Dados), e entrega aos clientes.

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfulgora.data.auth.UserManager
import com.example.myfulgora.data.remote.GrpcClass
import com.example.myfulgora.data.model.BikeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.myfulgora.data.helpers.NotificationManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val bikeState: BikeState) : HomeUiState()
}

class MotaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val grpcClient = GrpcClass()

    private var alreadyNotifiedBattery = false

    init {
        viewModelScope.launch {
            UserManager.activeBikeIndexFlow.collectLatest {
                fetchMotaData()
            }
        }

        viewModelScope.launch {
            while (true) {
                fetchMotaData()
                delay(10000) // 10 segundos
            }
        }
    }

    private fun fetchMotaData() {
        val currentBike = UserManager.getCurrentBike() ?: return
        val vin = currentBike.vin

        viewModelScope.launch {
            Log.d("MotaViewModel", "🔄 A atualizar dados para o VIN: $vin")

            val response = grpcClient.getMotaInfo(vin)

            if (response != null) {
                Log.d("MotaViewModel", "✅ Dados gRPC recebidos com sucesso")
                val total = UserManager.currentUser?.bikes?.size ?: 0

                val newState = BikeState(
                    bikeName = currentBike.name,
                    totalBikes = total,
                    isOnline = true,
                    batteryPercentage = response.batteryLevel,
                    range = response.batteryRange,
                    isCharging = response.isCharging,
                    drivingMode = response.drivingMode.name,
                    averageSpeed = response.averageSpeed,
                    consumption = response.batteryConsumption.toDouble(),
                    tyreFront = response.tyreFront.toInt(),
                    tyreBack = response.tyreBack.toInt(),
                    batteryTemp = response.batteryTemperature.toDouble(),
                    batteryCycles = response.batteryCycles,
                    avgConsumption = response.energyConsumptionAvg.toDouble(),
                    timeLeft = response.chargingTime,
                    latitude = response.latitude,
                    longitude = response.longitude,
                    documents = currentBike.documents?.toMap() ?: emptyMap()
                )

                _uiState.value = HomeUiState.Success(newState)
                checkBatteryNotifications(response.batteryLevel)
            } else {
                Log.w("MotaViewModel", "⚠️ Falha gRPC. A usar dados locais (Offline)")
                atualizarEstadoLocal(isActuallyOnline = false)
            }
        }
    }

    private fun checkBatteryNotifications(batteryLevel: Int) {
        if (batteryLevel <= 20 && !alreadyNotifiedBattery) {
            NotificationManager.addNotification(
                title = "Bateria Fraca",
                message = "A bateria desceu para ${batteryLevel}%. Planeia o carregamento."
            )
            alreadyNotifiedBattery = true
        } else if (batteryLevel > 20) {
            alreadyNotifiedBattery = false
        }
    }

    private fun atualizarEstadoLocal(isActuallyOnline: Boolean = false) {
        val currentBike = UserManager.getCurrentBike()
        val total = UserManager.currentUser?.bikes?.size ?: 0

        val newState = if (currentBike != null) {
            BikeState(
                bikeName = currentBike.name,
                totalBikes = total,
                isOnline = isActuallyOnline,
                batteryPercentage = currentBike.batteryLevel,
                range = currentBike.batteryRange.toInt(),
                // 👇 CORREÇÃO 2: Mantém os manuais no ecrã mesmo se a net for abaixo
                documents = currentBike.documents?.toMap() ?: emptyMap()
            )
        } else {
            BikeState(bikeName = "No Bike", totalBikes = total, isOnline = false)
        }

        _uiState.value = HomeUiState.Success(newState)
    }

    fun motaSeguinte() {
        UserManager.nextBike()
    }

    fun motaAnterior() {
        UserManager.previousBike()
    }

    fun setDrivingMode(mode: String) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            if (currentState.bikeState.drivingMode != mode) {
                Log.d("MotaViewModel", "Changing driving mode to: $mode")
                _uiState.value = HomeUiState.Success(
                    currentState.bikeState.copy(drivingMode = mode)
                )
                // Aqui no futuro podes enviar o comando via gRPC para a mota
                // grpcClient.setDrivingMode(vin, mode)
            }
        }
    }

    fun guardarDocumento(nomeDocumento: String, uri: String) {
        val currentBike = UserManager.getCurrentBike()
        if (currentBike != null) {
            // 👇 CORREÇÃO 3: Protege o mapa antes de adicionar
            if (currentBike.documents == null) {
                currentBike.documents = mutableMapOf()
            }
            currentBike.documents!![nomeDocumento] = uri

            _uiState.update { currentState ->
                if (currentState is HomeUiState.Success) {
                    val updatedBikeState = currentState.bikeState.copy(
                        documents = currentBike.documents!!.toMap()
                    )
                    HomeUiState.Success(updatedBikeState)
                } else {
                    currentState
                }
            }
        }
    }
}