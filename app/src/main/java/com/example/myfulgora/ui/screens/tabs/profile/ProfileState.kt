package com.example.myfulgora.ui.screens.tabs.profile

// Estado da UI para o ecrã de perfil atualizado com sincronização
data class ProfileState(
    val name: String = "A carregar...",
    val email: String = "...",
    val photoUri: String? = null,
    val bikeName: String = "Nenhuma mota associada",
    val bikeVin: String = "---",
    val isBikeConnected: Boolean = false,
    val totalBikes: Int = 0,             // Quantas motas tem na garagem
    val isSyncing: Boolean = false,      // Estado de carregamento da sincronização
    val showSyncSuccess: Boolean = false // Mostrar mensagem de sucesso
)