package com.example.myfulgora.ui.screens.tabs.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfulgora.data.auth.UserManager
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
                isBikeConnected = currentBike?.isConnected ?: false,
                photoUri = currentUser.profile.photoUri,
                totalBikes = currentUser.bikes.size
            )
        }
    }

    fun atualizarDado(tipo: String, novoValor: String) {
        when (tipo) {
            "Edit Name" -> UserManager.updateProfile(newName = novoValor)
            "Edit Email" -> UserManager.updateProfile(newEmail = novoValor)
        }
        carregarDadosDoUtilizador()
    }

    fun atualizarFoto(uri: String) {
        UserManager.updateProfile(newPhotoUri = uri)
        carregarDadosDoUtilizador()
    }

    fun sincronizarNovaMota(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            
            // Simulação de ligação ao servidor
            delay(2000)

            // Chamamos a função que sincroniza sem duplicar
            UserManager.syncBikesFromDatabase(context)
            
            // Recarregamos os dados na UI
            carregarDadosDoUtilizador()

            _uiState.value = _uiState.value.copy(isSyncing = false, showSyncSuccess = true)

            delay(3000)
            _uiState.value = _uiState.value.copy(showSyncSuccess = false)
        }
    }
}
