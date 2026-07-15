package com.uth.cloudcontacts.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uth.cloudcontacts.data.local.SessionManager
import com.uth.cloudcontacts.data.network.RetrofitClient
import com.uth.cloudcontacts.data.network.model.ActualizarUsuarioDto
import kotlinx.coroutines.launch

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {
    var email by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var cuentaEliminada by mutableStateOf(false)

    private val sessionManager = SessionManager(application)

    fun cargarEmail() {
        email = sessionManager.getUserEmail() ?: ""
    }

    fun actualizarEmail(userId: Int, nuevoEmail: String) {
        if (nuevoEmail.isBlank() || !nuevoEmail.contains("@")) {
            errorMessage = "Email inválido"
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            isSuccess = false
            try {
                val request = ActualizarUsuarioDto(id = userId, email = nuevoEmail, status = 1)
                val response = RetrofitClient.getApiService(getApplication())
                    .actualizarUsuario(request)
                if (response.isSuccessful) {
                    sessionManager.saveUserEmail(nuevoEmail)
                    isSuccess = true
                    email = nuevoEmail
                } else {
                    errorMessage = "Error al actualizar"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun eliminarCuenta(userId: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.getApiService(getApplication())
                    .eliminarUsuario(userId)
                if (response.isSuccessful) {
                    sessionManager.clearSession()
                    RetrofitClient.reset()
                    cuentaEliminada = true
                } else {
                    errorMessage = "Error al eliminar cuenta"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
