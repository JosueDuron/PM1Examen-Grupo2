package com.uth.cloudcontacts.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.cloudcontacts.data.network.RetrofitClient
import com.uth.cloudcontacts.data.network.model.AuthRequest
import com.uth.cloudcontacts.data.network.model.AuthResponse
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var authData by mutableStateOf<AuthResponse?>(null)

    fun login(onSuccess: (AuthResponse) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Completa todos los campos"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.postLogin(AuthRequest(email, password))
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null && data.id > 0) {
                        authData = data
                        onSuccess(data)
                    } else {
                        errorMessage = data?.mensaje ?: "Usuario o contraseña incorrectos"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = if (!errorBody.isNullOrBlank()) errorBody else "Credenciales incorrectas (401)"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
