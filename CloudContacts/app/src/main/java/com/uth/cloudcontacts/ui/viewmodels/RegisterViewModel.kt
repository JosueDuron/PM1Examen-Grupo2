package com.uth.cloudcontacts.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.cloudcontacts.data.network.RetrofitClient
import com.uth.cloudcontacts.data.network.model.RegisterRequest
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    fun register(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Completa todos los campos"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.postRegister(RegisterRequest(email, password))
                if (response.isSuccessful) {
                    isSuccess = true
                    onSuccess()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    errorMessage = if (!errorMsg.isNullOrBlank()) errorMsg else "Error en el registro: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
