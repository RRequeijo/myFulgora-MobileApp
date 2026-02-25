package com.example.myfulgora.data.auth

import android.content.Context
import com.example.myfulgora.data.model.MockBike
import com.example.myfulgora.data.model.MockDatabase
import com.example.myfulgora.data.model.MockUser
import com.google.gson.Gson
import java.io.InputStreamReader


object UserManager {
    var currentUser: MockUser? = null
        private set

    // NOVO: Guarda o índice da mota ativa (0 = 1ª mota, 1 = 2ª mota, etc.)
    var activeBikeIndex: Int = 0
        private set

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
        val users = loadMockUsers(context)
        val matchedUser = users.find { it.username == username && it.pass == pass }

        if (matchedUser != null) {
            currentUser = matchedUser
            activeBikeIndex = 0 // Sempre que faz login, a mota ativa é a primeira
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

    // --- MAGIA DA GARAGEM ---

    // Função segura para obter a mota que estamos a ver neste momento
    fun getCurrentBike(): MockBike? {
        val user = currentUser ?: return null
        if (user.bikes.isEmpty()) return null

        // Prevenção de segurança caso o índice fique desajustado
        if (activeBikeIndex >= user.bikes.size) activeBikeIndex = 0

        return user.bikes[activeBikeIndex]
    }

    // Estas duas funções vão ser usadas no teu ecrã Home para as setinhas! ⬅️ ➡️
    fun nextBike() {
        currentUser?.let { user ->
            if (user.bikes.isNotEmpty()) {
                activeBikeIndex = (activeBikeIndex + 1) % user.bikes.size
            }
        }
    }

    fun previousBike() {
        currentUser?.let { user ->
            if (user.bikes.isNotEmpty()) {
                activeBikeIndex = if (activeBikeIndex - 1 < 0) user.bikes.size - 1 else activeBikeIndex - 1
            }
        }
    }

    // Função para simular a adição de uma mota nova vinda do servidor
    fun addBikeFromDatabase(newBike: MockBike) {
        currentUser?.bikes?.add(newBike)
    }

    fun logout() {
        currentUser = null
        activeBikeIndex = 0
    }
}