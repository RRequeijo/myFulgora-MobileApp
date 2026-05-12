package com.example.myfulgora.data.model

// Estado da UI para o ecrã de perfil atualizado com sincronização
data class ProfileState(
    val name: String = "Rider",
    val email: String = "No email",
    val photoUri: String? = null,
    val bikeName: String = "Nenhuma mota associada",
    val bikeVin: String = "No vin",
    val isBikeConnected: Boolean = false,
    val totalBikes: Int = 0,             // Quantas motas tem na garagem
    val isSyncing: Boolean = false,      // Estado de carregamento da sincronização
    val syncMessage: String? = null // Mostrar mensagem de sucesso
)