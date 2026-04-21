package com.example.myfulgora.data.helpers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// O modelo de dados que o teu TopBar usa para ler as notificações
data class FulgoraNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false
)

object NotificationManager {
    // A lista que o teu "Sininho" fica a observar
    private val _notifications = MutableStateFlow<List<FulgoraNotification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    // 👇 A FUNÇÃO QUE FALTAVA (Que o MotaViewModel está a tentar chamar)
    fun addNotification(title: String, message: String) {
        // Pega na hora atual (ex: "11:05")
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val newNotification = FulgoraNotification(
            id = UUID.randomUUID().toString(), // Gera um ID único
            title = title,
            message = message,
            time = currentTime,
            isRead = false // Nasce sempre como "Não lida" (Bolinha vermelha)
        )

        // Adiciona a nova notificação ao topo da lista
        _notifications.update { currentList ->
            listOf(newNotification) + currentList
        }
    }

    // A função que o teu TopBar usa quando clicas em "Marcar como lidas"
    fun markAllAsRead() {
        _notifications.update { list ->
            list.map { it.copy(isRead = true) }
        }
    }

    // (No futuro, é aqui que vamos colocar a função de ler do Servidor gRPC)
}