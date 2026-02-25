package com.example.myfulgora.data.model

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
    @SerializedName("bike") val bike: MockBike // 👈 Como disseste, há SEMPRE uma mota, não é nullable (?)
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
    @SerializedName("isConnected") val isConnected: Boolean,
    @SerializedName("isLocked") val isLocked: Boolean
)