package com.example.myfulgora.ui.screens.tabs.profile


import androidx.lifecycle.ViewModel
import com.example.myfulgora.data.auth.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DocumentationState(
    // Mapa que diz: "DUA" -> "uri_do_ficheiro", "Seguro" -> null (se ainda não tiver)
    val savedDocuments: Map<String, String> = emptyMap()
)

class DocumentationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentationState())
    val uiState: StateFlow<DocumentationState> = _uiState.asStateFlow()

    init {
        carregarDocumentos()
    }

    private fun carregarDocumentos() {
        val currentBike = UserManager.getCurrentBike()
        if (currentBike != null) {
            _uiState.value = DocumentationState(
                savedDocuments = currentBike.documents?.toMap() ?: emptyMap()
            )
        }
    }

    fun guardarDocumento(nomeDocumento: String, uri: String) {
        val currentBike = UserManager.getCurrentBike()
        if (currentBike != null) {
            // 👇 SE O MAPA FOR NULO, CRIA UM NOVO ANTES DE GUARDAR
            if (currentBike.documents == null) {
                currentBike.documents = mutableMapOf()
            }

            currentBike.documents[nomeDocumento] = uri // Guarda na memória
            carregarDocumentos() // Atualiza o ecrã
        }
    }
}