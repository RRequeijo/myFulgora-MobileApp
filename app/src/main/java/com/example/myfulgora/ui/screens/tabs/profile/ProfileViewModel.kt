package com.example.myfulgora.ui.screens.tabs.profile

import androidx.lifecycle.ViewModel
import com.example.myfulgora.data.auth.UserManager // Importante!
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

    // A nossa "caixa" com o estado inicial
    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    init {
        // Assim que este ViewModel nasce, vai carregar os dados
        carregarDadosDoUtilizador()
    }

    private fun carregarDadosDoUtilizador() {
        // Vai buscar o user que fez login e está guardado na memória
        val currentUser = UserManager.currentUser

        if (currentUser != null) {
            // Se encontrou alguém, atualiza o ProfileState com os dados reais do JSON!
            _uiState.value = ProfileState(
                name = currentUser.profile.name,
                email = currentUser.profile.email,
                bikeName = currentUser.bike.name,
                bikeVin = currentUser.bike.vin,
                isBikeConnected = currentUser.bike.isConnected
            )
        } else {
            // Se por algum motivo for null, mantém os dados de "A carregar..."
            // (Ou podes pôr valores de erro aqui)
        }
    }

    fun atualizarDado(tipo: String, novoValor: String) {
        when (tipo) {
            "Edit Name" -> UserManager.updateProfile(newName = novoValor, newEmail = null)
            "Edit Email" -> UserManager.updateProfile(newName = null, newEmail = novoValor)
            "Change Password" -> {
                // Num mock simples, podemos ignorar a password ou criar uma função similar
            }
        }
        // Recarregar o State para a UI perceber que mudou
        carregarDadosDoUtilizador()
    }
}