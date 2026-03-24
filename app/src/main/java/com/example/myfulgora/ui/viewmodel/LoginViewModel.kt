package com.example.myfulgora.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfulgora.data.auth.AuthManager
import com.example.myfulgora.data.auth.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estados possíveis do ecrã de login
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    fun fazerLogin(user: String, pass: String) {

        // 1. Validação básica antes de tentar a internet
        if (user.isBlank() || pass.isBlank()) {
            _loginState.value = LoginState.Error("Por favor preenche todos os campos.")
            return
        }

        // 2. MODO DE TESTE RÁPIDO (A "Porta das Traseiras" para testar o gRPC)
        // Como apagámos o JSON, basta escreveres test / test na app para entrar logo!
        if (user == "test" && pass == "test") {
            UserManager.setupTestUser() // Prepara a memória com os dados iniciais
            _loginState.value = LoginState.Success
            return
        }

        // 3. O MODO REAL (Keycloak)
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                // Instanciar o AuthManager usando o contexto da aplicação
                val authManager = AuthManager(getApplication())

                // Tentar login real
                val sucesso = authManager.loginDireto(user, pass)

                if (sucesso) {
                    // Nota: No futuro, quando o Keycloak funcionar a 100%,
                    // terás de guardar os dados reais do utilizador no UserManager aqui!
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Login falhou. Verifica os dados.")
                    // Volta ao estado Idle para o utilizador poder tentar novamente
                    _loginState.value = LoginState.Idle
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Erro ao contactar o servidor: ${e.message}")
                _loginState.value = LoginState.Idle
            }
        }
    }
}