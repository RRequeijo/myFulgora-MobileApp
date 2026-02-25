package com.example.myfulgora.data.auth

import android.content.Context
import com.example.myfulgora.data.model.MockDatabase
import com.example.myfulgora.data.model.MockUser
import com.google.gson.Gson
import java.io.InputStreamReader

object UserManager {
    var currentUser: MockUser? = null
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

    fun logout() {
        currentUser = null
    }
}