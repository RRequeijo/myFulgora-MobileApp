package com.example.myfulgora.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfulgora.data.auth.UserManager
import com.example.myfulgora.data.model.ProfileState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    init {
        carregarDadosDoUtilizador()
    }

    private fun carregarDadosDoUtilizador() {
        val currentUser = UserManager.currentUser
        val currentBike = UserManager.getCurrentBike()

        if (currentUser != null) {
            _uiState.value = _uiState.value.copy(
                name = currentUser.profile.name,
                email = currentUser.profile.email,
                bikeName = currentBike?.name ?: "No Motorcycle",
                bikeVin = currentBike?.vin ?: "---",
                isBikeConnected = currentBike != null,
                photoUri = currentUser.profile.photoUri,
                totalBikes = currentUser.bikes.size
            )
        }
    }

    fun atualizarDado(tipo: String, novoValor: String) {
        val user = UserManager.currentUser ?: return
        when (tipo) {
            "Edit Name" -> user.profile.name = novoValor
            "Edit Email" -> user.profile.email = novoValor
        }
        carregarDadosDoUtilizador()
    }

    fun atualizarFoto(uri: String) {
        UserManager.currentUser?.profile?.photoUri = uri
        carregarDadosDoUtilizador()
    }

    fun sincronizarNovaMota(context: Context) {
        viewModelScope.launch {
            // 1. Iniciar feedback de progresso
            _uiState.value = _uiState.value.copy(isSyncing = true, syncMessage = null)

            try {
                // Simulação de espera de rede
                delay(1500)

                // PARA TESTES: Forçamos erro enquanto não tens servidor gRPC ativo
                // Se quiseres testar sucesso, comenta a linha abaixo.
                throw Exception("Server connection failed")

                /* 
                // Lógica Real futura:
                val user = UserManager.currentUser ?: return@launch
                val quantidadeLocal = user.bikes.size
                
                // Chamada gRPC aqui...
                
                carregarDadosDoUtilizador()
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    syncMessage = "Garage updated successfully!"
                )
                */

            } catch (e: Exception) {
                // Feedback de erro para o utilizador
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    syncMessage = "Error: ${e.message}"
                )
            }

            // Limpar mensagem após alguns segundos
            delay(3500)
            _uiState.value = _uiState.value.copy(syncMessage = null)
        }
    }
}