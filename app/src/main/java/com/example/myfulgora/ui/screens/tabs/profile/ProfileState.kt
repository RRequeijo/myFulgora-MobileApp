package com.example.myfulgora.ui.screens.tabs.profile

// Estado da UI para o ecrã de perfil
data class ProfileState(
    val name: String = "A carregar...",
    val email: String = "...",
    val bikeName: String = "Nenhuma mota associada",
    val bikeVin: String = "---",
    val isBikeConnected: Boolean = false,
    val photoUri: String? = null
)