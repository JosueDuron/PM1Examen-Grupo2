package com.uth.cloudcontacts.ui.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uth.cloudcontacts.data.network.RetrofitClient
import com.uth.cloudcontacts.data.network.model.ContactoRequest
import com.uth.cloudcontacts.domain.model.Contacto
import kotlinx.coroutines.launch
import java.io.InputStream

class DetalleContactoViewModel(application: Application) : AndroidViewModel(application) {
    var contacto by mutableStateOf<Contacto?>(null)
    var isLoading by mutableStateOf(false)
    var isUpdating by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var updateSuccess by mutableStateOf(false)
    var isDeleting by mutableStateOf(false)
    var eliminado by mutableStateOf(false)

    var nombre by mutableStateOf("")
    var telefono by mutableStateOf("")
    var direccion by mutableStateOf("")
    var latitud by mutableStateOf(0.0)
    var longitud by mutableStateOf(0.0)
    var firmaBase64 by mutableStateOf("")
    var imagenBase64 by mutableStateOf("")

    fun fetchContacto(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.getApiService(getApplication())
                    .obtenerContactoPorId(id)

                if (response.isSuccessful) {
                    contacto = response.body()
                    contacto?.let { c ->
                        nombre = c.nombre
                        telefono = c.telefono
                        direccion = c.direccion
                        latitud = c.latitud
                        longitud = c.longitud
                        firmaBase64 = c.firmaBase64 ?: ""
                        imagenBase64 = c.imagenBase64 ?: ""
                    }
                } else {
                    errorMessage = "Error al cargar contacto"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // MÉTODO AGREGADO - Formateo de teléfono
    fun onTelefonoChange(newValue: String) {
        val digitsOnly = newValue.filter { it.isDigit() }
        telefono = when {
            digitsOnly.length <= 4 -> digitsOnly
            digitsOnly.length <= 8 -> "${digitsOnly.substring(0, 4)}-${digitsOnly.substring(4)}"
            else -> "${digitsOnly.substring(0, 4)}-${digitsOnly.substring(4, 8)}"
        }
    }

    fun onImageSelected(inputStream: InputStream?) {
        inputStream?.let {
            val bitmap = BitmapFactory.decodeStream(it)
            val outputStream = java.io.ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val bytes = outputStream.toByteArray()
            imagenBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }

    fun actualizarContacto() {
        viewModelScope.launch {
            isUpdating = true
            errorMessage = null
            try {
                val request = ContactoRequest(
                    id = contacto?.id,
                    nombre = nombre,
                    telefono = telefono,
                    direccion = direccion,
                    latitud = latitud,
                    longitud = longitud,
                    firmaBase64 = firmaBase64,
                    imagenBase64 = imagenBase64,
                    usuarioId = contacto?.usuarioId ?: 0
                )

                val response = RetrofitClient.getApiService(getApplication())
                    .actualizarContacto(request)

                if (response.isSuccessful) {
                    updateSuccess = true
                    fetchContacto(contacto?.id ?: 0)
                } else {
                    errorMessage = "Error al actualizar"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isUpdating = false
            }
        }
    }

    fun resetUpdateState() {
        updateSuccess = false
    }

    fun eliminarContacto() {
        viewModelScope.launch {
            isDeleting = true
            errorMessage = null
            try {
                val response = RetrofitClient.getApiService(getApplication())
                    .eliminarContacto(contacto?.id ?: 0)
                if (response.isSuccessful) {
                    eliminado = true
                } else {
                    errorMessage = "Error al eliminar el contacto"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isDeleting = false
            }
        }
    }
}
