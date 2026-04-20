package com.example.myfulgora.data.helpers

import com.example.myfulgora.data.model.FulgoraNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// O 'object' significa que só existe UMA cópia deste gestor na app inteira (Singleton).
object NotificationManager {
    private val _notifications = MutableStateFlow<List<FulgoraNotification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    // O teu cliente gRPC
    // private val grpcClient = GrpcClass()

    // Chamas isto quando a app arranca (ex: no MainScreen)
    fun fetchNotificationsFromServer(scope: CoroutineScope) {
        scope.launch {
            try {
                // val response = grpcClient.getNotifications()
                // _notifications.value = response...
            } catch (e: Exception) {
                // erro
            }
        }
    }

    fun markAllAsRead() {
        _notifications.update { list ->
            list.map { it.copy(isRead = true) }
        }
        // E avisas o servidor em pano de fundo:
        // grpcClient.markAllAsRead()
    }
}