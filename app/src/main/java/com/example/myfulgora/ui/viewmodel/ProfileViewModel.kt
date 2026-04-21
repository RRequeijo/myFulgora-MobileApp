package com.example.myfulgora.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.myfulgora.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfulgora.data.auth.UserManager
import com.example.myfulgora.data.model.ProfileState
// import com.example.myfulgora.data.remote.GrpcClass // <- Descomenta quando tiveres o gRPC pronto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    // 1. Instanciamos o cliente gRPC (como fizeste no MotaViewModel)
    // private val grpcClient = GrpcClass()

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
                bikeName = currentBike?.name ?: "",
                bikeVin = currentBike?.vin ?: "---",
                isBikeConnected = currentBike != null,
                photoUri = currentUser.profile.photoUri,
                totalBikes = currentUser.bikes.size
            )
        }
    }

    // =====================================================================
    // ATUALIZAR NOME E EMAIL
    // =====================================================================
    fun atualizarDado(tipo: String, novoValor: String, context: Context) {
        val user = UserManager.currentUser ?: return

        // Guardamos os valores antigos para o caso de a internet falhar (Rollback)
        val nomeAntigo = user.profile.name
        val emailAntigo = user.profile.email

        // 1. Atualização Otimista: Muda na UI instantaneamente
        when (tipo) {
            context.getString(R.string.profile_edit_name) -> user.profile.name = novoValor
            context.getString(R.string.profile_edit_email) -> user.profile.email = novoValor
        }
        carregarDadosDoUtilizador()

        // 2. Avisamos o Servidor em pano de fundo
        viewModelScope.launch {
            try {
                // 👇 A TUA CHAMADA GRPC ENTRARÁ AQUI
                // grpcClient.updateProfileInfo(user.profile.name, user.profile.email)

                Log.d("ProfileViewModel", "✅ $tipo guardado no servidor.")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "❌ Erro de rede ao guardar $tipo.", e)

                // 3. Rollback: Se der erro, revertemos para o valor antigo
                user.profile.name = nomeAntigo
                user.profile.email = emailAntigo
                carregarDadosDoUtilizador()

                // Aqui no futuro podes criar um estado para mostrar um Toast de erro na UI
            }
        }
    }

    // =====================================================================
    // ATUALIZAR FOTO DE PERFIL
    // =====================================================================
    // Nota: Adicionei o "context" porque precisas dele para converter a imagem em Bytes!
    fun atualizarFoto(uri: String, context: Context) {
        val user = UserManager.currentUser ?: return
        val fotoAntiga = user.profile.photoUri

        // 1. Atualização Otimista
        user.profile.photoUri = uri
        carregarDadosDoUtilizador()

        viewModelScope.launch {
            try {
                // 👇 A TUA CHAMADA GRPC ENTRARÁ AQUI

                // Exemplo de como converter a URI num ByteArray (para enviar via gRPC)
                /*
                val bytesDaImagem = context.contentResolver.openInputStream(Uri.parse(uri))?.readBytes()
                if (bytesDaImagem != null) {
                    grpcClient.updateProfilePhoto(bytesDaImagem, "jpg")
                }
                */

                Log.d("ProfileViewModel", "✅ Foto de perfil sincronizada com o servidor.")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "❌ Erro ao enviar foto.", e)

                // Rollback
                user.profile.photoUri = fotoAntiga
                carregarDadosDoUtilizador()
            }
        }
    }

    // =====================================================================
    // SINCRONIZAR GARAGEM
    // =====================================================================
    fun sincronizarNovaMota(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true, syncMessage = null)

            try {
                delay(1500)
                throw Exception("Server connection failed")

                /* // Lógica Real futura:
                // ...
                */

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    syncMessage = "Error: ${e.message}"
                )
            }

            delay(3500)
            _uiState.value = _uiState.value.copy(syncMessage = null)
        }
    }
}