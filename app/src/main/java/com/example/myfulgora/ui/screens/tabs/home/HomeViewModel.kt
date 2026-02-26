package com.example.myfulgora.ui.screens.tabs.home

import androidx.lifecycle.ViewModel
import com.example.myfulgora.data.auth.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// O Estado que a Home vai ler
data class HomeState(
    val bikeName: String = "A carregar...",
    val batteryLevel: Int = 0,
    val isConnected: Boolean = false,
    val totalBikes: Int = 0 
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        atualizarEcra()
    }

    fun atualizarEcra() {
        val currentBike = UserManager.getCurrentBike()
        val total = UserManager.currentUser?.bikes?.size ?: 0

        if (currentBike != null) {
            _uiState.value = HomeState(
                bikeName = currentBike.name,
                batteryLevel = currentBike.batteryLevel,
                isConnected = currentBike.isConnected,
                totalBikes = total
            )
        }
    }

    // Função para a seta da direita ➡️
    fun motaSeguinte() {
        UserManager.nextBike()
        atualizarEcra() 
    }

    // Função para a seta da esquerda ⬅️
    fun motaAnterior() {
        UserManager.previousBike()
        atualizarEcra()
    }
}