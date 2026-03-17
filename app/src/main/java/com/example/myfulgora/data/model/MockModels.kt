package com.example.myfulgora.data.model

import android.R
import com.google.gson.annotations.SerializedName

// 1. A raiz do ficheiro JSON (A lista de utilizadores)
data class MockDatabase(
    @SerializedName("users") val users: List<MockUser>
)

// 2. O Utilizador
data class MockUser(
    @SerializedName("username") val username: String,
    @SerializedName("password") val pass: String, // Só para testarmos o login localmente
    @SerializedName("profile") val profile: MockProfile,
    @SerializedName("bikes") val bikes: MutableList<MockBike>
)

// 3. O Perfil Pessoal
data class MockProfile(
    @SerializedName("name") var name: String,
    @SerializedName("email") var email: String,
    var photoUri: String? = null
)

// 4. A Mota
data class MockBike(
    @SerializedName("vin") val vin: String,
    @SerializedName("name") val name: String,
    @SerializedName("batteryLevel") val batteryLevel: Int,
    @SerializedName("batteryHealth") val batteryHealth: String,
    @SerializedName("batteryTemperature") val batteryTemperature: Double,
    @SerializedName("batteryConsumption") val batteryConsumption: Double,
    @SerializedName("batteryCycles") val batteryCycles: Int,
    @SerializedName("batteryRange") val batteryRange: Double,
    @SerializedName("chargingHours") val chargingHours: Int,
    @SerializedName("chargingMinutes") val chargingMinutes: Int,
    @SerializedName("isCharging") val isCharging: Boolean,
    @SerializedName("isConnected") val isConnected: Boolean,
    @SerializedName("isLocked") val isLocked: Boolean,
    @SerializedName("drivingMode") val drivingMode: String,
    @SerializedName("tyreFront") val tyreFront: Int,
    @SerializedName("tyreBack") val tyreBack: Int,
    @SerializedName("energyConsumption") val energyConsumption: Double,
    @SerializedName("averageSpeed") val averageSpeed: Int,
    // A nossa "gaveta" dos documentos que não vem no JSON (tem de ter o '?')
    var documents: MutableMap<String, String> = mutableMapOf()

)

// 5. Notificações
data class FulgoraNotification(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean
)