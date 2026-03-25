package com.example.myfulgora.data.model

data class FulgoraNotification(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false
)
