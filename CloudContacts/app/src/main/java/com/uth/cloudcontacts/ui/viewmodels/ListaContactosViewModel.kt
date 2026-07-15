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

enum class Ordenamiento { NOMBRE_ASC, NOMBRE_DESC, FECHA_RECENTE, FECHA_ANTIGUA }

class ListaContactosViewModel(application: Application) : AndroidViewModel(application) {
    var contactos by mutableStateOf<List<Contacto>>(emptyList())
    var isLoading by mutableStateOf(false)
    var isRefreshing by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var searchQuery by mutableStateOf("")
    var isSearching by mutableStateOf(false)
    var ordenamiento by mutableStateOf(Ordenamiento.FECHA_RECENTE)

    fun fetchContactos(usuarioId: Int, fromRefresh: Boolean = false) {
        viewModelScope.launch {
            if (fromRefresh) isRefreshing = true else isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.getApiService(getApplication())
                    .obtenerContactos(usuarioId)

                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    contactos = body
                    ordenarContactos(ordenamiento)
                } else {
                    errorMessage = "Error al cargar contactos: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                if (fromRefresh) isRefreshing = false else isLoading = false
            }
        }
    }

    fun ordenarContactos(orden: Ordenamiento) {
        ordenamiento = orden
        contactos = when (orden) {
            Ordenamiento.NOMBRE_ASC -> contactos.sortedBy { it.nombre }
            Ordenamiento.NOMBRE_DESC -> contactos.sortedByDescending { it.nombre }
            Ordenamiento.FECHA_RECENTE -> contactos.sortedByDescending { it.fechaCreacion }
            Ordenamiento.FECHA_ANTIGUA -> contactos.sortedBy { it.fechaCreacion }
        }
    }

    fun buscarContactos(texto: String, usuarioId: Int) {
        if (texto.isBlank()) {
            isSearching = false
            fetchContactos(usuarioId)
            return
        }
        viewModelScope.launch {
            isLoading = true
            isSearching = true
            errorMessage = null
            try {
                val response = RetrofitClient.getApiService(getApplication())
                    .buscarContactos(texto, usuarioId)
                if (response.isSuccessful) {
                    contactos = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error en la búsqueda"
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
