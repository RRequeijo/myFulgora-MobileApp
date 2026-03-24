package com.example.myfulgora.data.auth

import android.util.Log
import com.example.myfulgora.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

//UserManager (O Rececionista): É o gajo à porta. Ele só sabe duas coisas: "Quem é o cliente que entrou?" (User) e "Em que mesa ele está sentado?" (A mota selecionada).

object UserManager {
    // Agora usamos classes REAIS, não Mocks
    var currentUser: User? = null
        private set

    private val _activeBikeIndex = MutableStateFlow(0)
    val activeBikeIndexFlow = _activeBikeIndex.asStateFlow()

    var activeBikeIndex: Int
        get() = _activeBikeIndex.value
        private set(value) {
            _activeBikeIndex.value = value
        }

    // 💡 IMPORTANTE: Lembras-te que o MotaViewModel fica à escuta deste Flow?
    // É aqui que a magia acontece quando mudas de mota!

    fun setupTestUser() {
        val testBike = Bike(
            vin = "MOTA-TESTE-123",
            name = "Fulgora LAB (gRPC Test)"
        )

        currentUser = User(
            username = "test",
            profile = UserProfile(name = "Tester gRPC", email = "test@fulgora.pt"),
            bikes = mutableListOf(testBike)
        )
        _activeBikeIndex.value = 0
        Log.d("UserManager", "🚀 Sessão de Teste Iniciada.")
    }

    fun getCurrentBike(): Bike? {
        val user = currentUser ?: return null
        if (user.bikes.isEmpty()) return null
        return user.bikes.getOrNull(activeBikeIndex)
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

    fun logout() {
        currentUser = null
        _activeBikeIndex.value = 0
    }
}