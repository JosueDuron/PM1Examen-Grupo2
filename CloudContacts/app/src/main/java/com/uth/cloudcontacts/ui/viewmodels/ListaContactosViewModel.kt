package com.uth.cloudcontacts.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uth.cloudcontacts.data.network.RetrofitClient
import com.uth.cloudcontacts.domain.model.Contacto
import kotlinx.coroutines.launch

class ListaContactosViewModel(application: Application) : AndroidViewModel(application) {
    var contactos by mutableStateOf<List<Contacto>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchContactos(usuarioId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.getApiService(getApplication())
                    .obtenerContactos(usuarioId)

                if (response.isSuccessful) {
                    contactos = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error al cargar contactos: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun eliminarContacto(contactoId: Int, usuarioId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.getApiService(getApplication())
                    .eliminarContacto(contactoId)

                if (response.isSuccessful) {
                    fetchContactos(usuarioId)
                } else {
                    errorMessage = "Error al eliminar"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }
}
