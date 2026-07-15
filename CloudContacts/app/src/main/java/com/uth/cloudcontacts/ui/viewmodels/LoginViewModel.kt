package com.uth.cloudcontacts.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uth.cloudcontacts.data.local.SessionManager
import com.uth.cloudcontacts.data.network.RetrofitClient
import com.uth.cloudcontacts.data.network.model.AuthRequest
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)
    var userId by mutableStateOf(0)

    private val sessionManager = SessionManager(application)

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Complete todos los campos"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.getApiService(getApplication())
                    .loginUsuario(AuthRequest(email, password))

                if (response.isSuccessful) {
                    response.body()?.let { auth ->
                        userId = auth.id
                        sessionManager.saveSession(auth.id, auth.token, email)
                        RetrofitClient.reset()
                        loginSuccess = true
                    } ?: run {
                        errorMessage = "Respuesta vacia del servidor"
                    }
                } else {
                    errorMessage = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetState() {
        loginSuccess = false
        errorMessage = null
    }
}
