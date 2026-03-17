package com.example.myfulgora.ui.viewmodel

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

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val bikeState: BikeState) : HomeUiState()
}

class MotaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    // Instância da classe gRPC que criaste
    private val grpcClient = GrpcClass()
    
    private var alreadyNotifiedBattery = false

    init {
        // 1. Observar mudança de mota no UserManager
        viewModelScope.launch {
            UserManager.activeBikeIndexFlow.collectLatest {
                fetchMotaData() // Sempre que muda a mota, pede dados novos
            }
        }

        // 2. Loop de atualização (opcional, para simular tempo real com gRPC)
        viewModelScope.launch {
            while (true) {
                fetchMotaData()
                delay(10000) // Atualiza de 10 em 10 segundos
            }
        }
    }

    /**
     * Esta função faz o pedido ao Servidor via gRPC
     */
    private fun fetchMotaData() {
        val currentBike = UserManager.getCurrentBike() ?: return
        val vin = currentBike.vin

        viewModelScope.launch {
            // Chamada ao teu método suspend no GrpcClass
            val response = grpcClient.getMotaInfo(vin)

            if (response != null) {
                // Se o servidor respondeu, atualizamos a UI com os dados REAIS do laboratório
                val total = UserManager.currentUser?.bikes?.size ?: 0
                
                val newState = BikeState(
                    bikeName = currentBike.name, // Nome vem do nosso JSON local
                    totalBikes = total,
                    isOnline = response.isConnected, // Usar o isConnected do proto
                    
                    // Dados que vêm do Servidor (ajustados conforme o teu .proto)
                    batteryPercentage = response.batteryLevel,
                    range = response.batteryRange, // O campo no proto é battery_range
                    isCharging = response.isCharging,
                    drivingMode = response.drivingMode.name, // Converter Enum para String
                    
                    // Outros dados do gRPC
                    averageSpeed = response.averageSpeed,
                    consumption = response.batteryConsumption.toDouble(),
                    tyreFront = response.tyreFront.toInt(),
                    tyreBack = response.tyreBack.toInt(),
                    batteryTemp = response.batteryTemperature.toDouble(),
                    batteryCycles = response.batteryCycles,
                    avgConsumption = response.energyConsumptionAvg.toDouble()
                )

                _uiState.value = HomeUiState.Success(newState)
                
                // Verificar Notificações
                checkBatteryNotifications(response.batteryLevel)
            } else {
                // Se o gRPC falhou (timeout ou servidor desligado), mostramos os dados locais (Mock)
                Log.w("GRPC", "Falha ao obter dados, a usar fallback local")
                atualizarEstadoLocal()
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

    private fun atualizarEstadoLocal() {
        val currentBike = UserManager.getCurrentBike()
        val total = UserManager.currentUser?.bikes?.size ?: 0

        val newState = if (currentBike != null) {
            BikeState(
                bikeName = currentBike.name,
                totalBikes = total,
                isOnline = currentBike.isConnected,
                batteryPercentage = currentBike.batteryLevel,
                range = currentBike.batteryRange.toInt()
            )
        } else {
            BikeState(bikeName = "No Bike", totalBikes = total)
        }

        _uiState.value = HomeUiState.Success(newState)
    }

    fun motaSeguinte() {
        UserManager.nextBike()
    }

    fun motaAnterior() {
        UserManager.previousBike()
    }
}
