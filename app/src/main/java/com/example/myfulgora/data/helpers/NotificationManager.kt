package com.example.myfulgora.data.helpers

import com.example.myfulgora.data.model.FulgoraNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// O 'object' significa que só existe UMA cópia deste gestor na app inteira (Singleton).
object NotificationManager {

    // 1. O Armazém Secreto: Uma lista que deteta mudanças automaticamente (StateFlow)
    private val _notifications = MutableStateFlow<List<FulgoraNotification>>(emptyList())

    // A montra pública (só de leitura) para a TopBar poder ver as notificações
    val notifications: StateFlow<List<FulgoraNotification>> = _notifications.asStateFlow()

    // 2. A Fábrica: Função para a mota criar uma notificação nova
    fun addNotification(title: String, message: String) {
        _notifications.update { listaAtual ->
            val novoId = (listaAtual.maxOfOrNull { it.id } ?: 0) + 1

            val novaNotificacao = FulgoraNotification(
                id = novoId,
                title = title,
                message = message,
                time = "Agora mesmo",
                isRead = false // Nasce sempre como NÃO LIDA (bolinha vermelha)
            )

            // Junta a nova notificação no topo da lista antiga
            listOf(novaNotificacao) + listaAtual
        }
    }

    // 3. A Limpeza: Função para quando o utilizador abre o popup
    fun markAllAsRead() {
        _notifications.update { listaAtual ->
            // Pega em todas as notificações e muda o 'isRead' para true
            listaAtual.map { it.copy(isRead = true) }
        }
    }
}