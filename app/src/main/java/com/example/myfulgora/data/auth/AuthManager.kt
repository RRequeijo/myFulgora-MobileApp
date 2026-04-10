package com.example.myfulgora.data.auth

import android.content.Context
import android.util.Log
import com.example.myfulgora.data.repository.TokenRepository
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class AuthManager(context: Context) {

    private val tokenRepository = TokenRepository(context)
    private val client = OkHttpClient()

    companion object {
        private const val KEYCLOAK_URL = "http://172.20.0.201:8080/realms/Amover/protocol/openid-connect/token"
        private const val CLIENT_ID = "mota-mobile"
    }

    suspend fun loginDireto(user: String, pass: String): Boolean {
        return withContext(Dispatchers.IO) {
            // Bypass para login local/teste sem necessidade de VPN/Rede
            if (user == "amover" && pass == "amover") {
                tokenRepository.saveToken("offline_test_token")
                Log.d("AuthManager", "Login local (offline) realizado com sucesso!")
                return@withContext true
            }
            //FIM DO BYPASS
            try {
                val formBody = FormBody.Builder()
                    .add("client_id", CLIENT_ID)
                    .add("username", user)
                    .add("password", pass)
                    .add("grant_type", "password")
                    .add("scope", "openid profile email")
                    .build()

                val request = Request.Builder()
                    .url(KEYCLOAK_URL)
                    .post(formBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val json = JSONObject(responseBody)
                    val accessToken = json.getString("access_token")

                    // Guardar o token de forma persistente no DataStore
                    tokenRepository.saveToken(accessToken)

                    Log.d("AuthManager", "Login com sucesso!")
                    return@withContext true
                } else {
                    Log.e("AuthManager", "Erro no login: ${response.code}")
                    return@withContext false
                }

            } catch (e: Exception) {
                Log.e("AuthManager", "Exceção de rede: ${e.message}")
                return@withContext false
            }
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return tokenRepository.accessToken.firstOrNull() != null
    }

    suspend fun logout() {
        tokenRepository.clearToken()
    }
}