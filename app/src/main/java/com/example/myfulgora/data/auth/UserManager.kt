package com.example.myfulgora.data.auth

import android.content.Context
import com.example.myfulgora.data.model.MockBike
import com.example.myfulgora.data.model.MockDatabase
import com.example.myfulgora.data.model.MockUser
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStreamReader

object UserManager {
    var currentUser: MockUser? = null
        private set

    // 1. TORNAR O ÍNDICE REATIVO com um StateFlow
    private val _activeBikeIndex = MutableStateFlow(0)
    val activeBikeIndexFlow = _activeBikeIndex.asStateFlow()

    var activeBikeIndex: Int
        get() = _activeBikeIndex.value
        private set(value) {
            _activeBikeIndex.value = value
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

    // 2. CORRIGIR O BUG DA SINCRONIZAÇÃO
    fun syncBikesFromDatabase(context: Context) {
        val users = loadMockUsers(context)
        val freshUser = users.find { it.username == currentUser?.username }
        if (freshUser != null) {
            // Limpa a lista atual e adiciona a lista 'fresca' do JSON, resolvendo o bug de duplicação
            currentUser?.bikes?.clear()
            currentUser?.bikes?.addAll(freshUser.bikes)
            
            // Garante que o índice não fica fora dos limites após a sincronização
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