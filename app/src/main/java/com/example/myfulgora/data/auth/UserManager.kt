package com.example.myfulgora.data.auth

import android.content.Context
import android.util.Log
import com.example.myfulgora.data.model.MockBike
import com.example.myfulgora.data.model.MockDatabase
import com.example.myfulgora.data.model.MockUser
import com.example.myfulgora.data.model.MockProfile
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStreamReader

object UserManager {
    var currentUser: MockUser? = null
        private set

    private val _activeBikeIndex = MutableStateFlow(0)
    val activeBikeIndexFlow = _activeBikeIndex.asStateFlow()

    var activeBikeIndex: Int
        get() = _activeBikeIndex.value
        private set(value) {
            _activeBikeIndex.value = value
        }

    /**
     * CONFIGURAÇÃO DE TESTE RÁPIDO PARA gRPC
     */
    fun setupTestUser() {
        val testBike = MockBike(
            vin = "MOTA-TESTE-123", // Altera se o teu script precisar de um VIN específico
            name = "Fulgora LAB (gRPC Test)",
            batteryLevel = 10,
            batteryHealth = "N/A",
            batteryTemperature = 0.0,
            batteryConsumption = 0.0,
            batteryCycles = 0,
            batteryRange = 0.0,
            chargingHours = 0,
            chargingMinutes = 0,
            isCharging = false,
            isConnected = true,
            isLocked = false,
            drivingMode = "Normal",
            tyreFront = 0,
            tyreBack = 0,
            energyConsumption = 0.0,
            averageSpeed = 0
        )

        currentUser = MockUser(
            username = "test",
            pass = "test",
            profile = MockProfile(name = "Tester gRPC", email = "test@fulgora.pt"),
            bikes = mutableListOf(testBike)
        )
        _activeBikeIndex.value = 0
        Log.d("UserManager", "🚀 Modo de Teste Ativado. Mota carregada para gRPC.")
    }

    private fun loadMockUsers(context: Context): List<MockUser> {
        return try {
            val inputStream = context.assets.open("mock_database.json")
            val reader = InputStreamReader(inputStream)
            val mockDatabase = Gson().fromJson(reader, MockDatabase::class.java)
            reader.close()
            mockDatabase.users
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun validateMockLogin(context: Context, username: String, pass: String): Boolean {
        // Se escreveres "test" / "test", ativa o modo gRPC direto
        if (username == "test" && pass == "test") {
            setupTestUser()
            return true
        }

        val users = loadMockUsers(context)
        val matchedUser = users.find { it.username == username && it.pass == pass }

        if (matchedUser != null) {
            currentUser = matchedUser
            _activeBikeIndex.value = 0
            return true
        }
        return false
    }

    fun updateProfile(newName: String? = null, newEmail: String? = null, newPhotoUri: String? = null) {
        currentUser?.let { user ->
            if (newName != null) user.profile.name = newName
            if (newEmail != null) user.profile.email = newEmail
            if (newPhotoUri != null) user.profile.photoUri = newPhotoUri
        }
    }

    fun getCurrentBike(): MockBike? {
        val user = currentUser ?: return null
        if (user.bikes.isEmpty()) return null
        if (activeBikeIndex >= user.bikes.size) _activeBikeIndex.value = 0
        return user.bikes[activeBikeIndex]
    }

    fun nextBike() {
        currentUser?.let { user ->
            if (user.bikes.isNotEmpty()) {
                _activeBikeIndex.value = (activeBikeIndex + 1) % user.bikes.size
            }
        }
    }

    fun previousBike() {
        currentUser?.let { user ->
            if (user.bikes.isNotEmpty()) {
                _activeBikeIndex.value = if (activeBikeIndex - 1 < 0) user.bikes.size - 1 else activeBikeIndex - 1
            }
        }
    }

    fun syncBikesFromDatabase(context: Context) {
        val users = loadMockUsers(context)
        val freshUser = users.find { it.username == currentUser?.username }
        if (freshUser != null) {
            currentUser?.bikes?.clear()
            currentUser?.bikes?.addAll(freshUser.bikes)
            if (activeBikeIndex >= (currentUser?.bikes?.size ?: 0)) {
                _activeBikeIndex.value = 0
            }
        }
    }

    fun logout() {
        currentUser = null
        _activeBikeIndex.value = 0
    }
}
