package com.uth.cloudcontacts.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uth.cloudcontacts.data.local.SessionManager
import com.uth.cloudcontacts.data.network.RetrofitClient
import com.uth.cloudcontacts.data.network.model.RegisterRequest
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var registerSuccess by mutableStateOf(false)

    private val sessionManager = SessionManager(application)

    fun register() {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "Complete todos los campos"
            return
        }
        if (password != confirmPassword) {
            errorMessage = "Las contraseñas no coinciden"
            return
        }
        if (password.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.getApiService(getApplication())
                    .registrarUsuario(RegisterRequest(email, password))

                if (response.isSuccessful) {
                    response.body()?.let { auth ->
                        sessionManager.saveSession(auth.id, auth.token)
                        RetrofitClient.reset()
                        registerSuccess = true
                    } ?: run {
                        errorMessage = "Registro exitoso pero sin respuesta"
                        registerSuccess = true
                    }
                } else {
                    errorMessage = "Error al registrar: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
