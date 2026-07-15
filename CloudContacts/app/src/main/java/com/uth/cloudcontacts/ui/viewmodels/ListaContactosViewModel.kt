package com.uth.cloudcontacts.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.cloudcontacts.data.network.RetrofitClient
import com.uth.cloudcontacts.data.network.model.ContactoResponse
import kotlinx.coroutines.launch

class ListaContactosViewModel : ViewModel() {

    var contactos by mutableStateOf<List<ContactoResponse>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchContactos(usuarioId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getContactosPorUsuario(usuarioId)
                if (response.isSuccessful) {
                    contactos = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error al obtener contactos: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
