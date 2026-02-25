package com.example.myfulgora.data.auth

import android.content.Context
import com.example.myfulgora.data.model.MockDatabase
import com.example.myfulgora.data.model.MockUser
import com.google.gson.Gson
import java.io.InputStreamReader

object UserManager {
    // 1. A variável de ouro: Guarda quem está "logado" neste momento.
    // Se for null, ninguém fez login.
    var currentUser: MockUser? = null
        private set // Apenas o UserManager pode alterar isto diretamente

    // 2. Lê o ficheiro JSON e devolve a lista de utilizadores
    private fun loadMockUsers(context: Context): List<MockUser> {
        return try {
            // Abre o ficheiro da pasta assets
            val inputStream = context.assets.open("mock_database.json")
            val reader = InputStreamReader(inputStream)

            // O Gson faz a magia de converter o texto no objeto MockDatabase
            val mockDatabase = Gson().fromJson(reader, MockDatabase::class.java)
            reader.close()

            mockDatabase.users
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Se der erro (ex: ficheiro não existe), devolve lista vazia
        }
    }

    // 3. Tenta fazer login com os dados inseridos
    fun validateMockLogin(context: Context, username: String, pass: String): Boolean {
        val users = loadMockUsers(context)

        // Procura se há algum utilizador com este username E password
        val matchedUser = users.find { it.username == username && it.pass == pass }

        if (matchedUser != null) {
            currentUser = matchedUser // Guarda o utilizador na memória global!
            return true
        }

        return false // Falhou o login
    }

    fun updateProfile(newName: String?, newEmail: String?) {
        currentUser?.let { user ->
            if (newName != null) user.profile.name = newName
            if (newEmail != null) user.profile.email = newEmail
        }
    }

    // 4. Limpa os dados ao sair
    fun logout() {
        currentUser = null
    }
}