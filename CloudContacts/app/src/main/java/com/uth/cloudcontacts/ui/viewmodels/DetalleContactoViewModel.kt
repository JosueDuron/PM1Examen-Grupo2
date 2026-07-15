package com.uth.cloudcontacts.ui.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.cloudcontacts.data.network.RetrofitClient
import com.uth.cloudcontacts.data.network.model.ContactoResponse
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

class DetalleContactoViewModel : ViewModel() {

    var contacto by mutableStateOf<ContactoResponse?>(null)
    var isLoading by mutableStateOf(false)
    var isUpdating by mutableStateOf(false)
    var updateSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Campos editables
    var nombre by mutableStateOf("")
    var telefono by mutableStateOf("")

    fun onTelefonoChange(newValue: String) {
        val digits = newValue.filter { it.isDigit() }
        if (digits.length <= 8) {
            telefono = if (digits.length > 4) {
                "${digits.take(4)}-${digits.substring(4)}"
            } else {
                digits
            }
        }
    }
    var direccion by mutableStateOf("")
    var latitud by mutableStateOf(0.0)
    var longitud by mutableStateOf(0.0)
    var imagenBase64 by mutableStateOf<String?>(null)
    var firmaBase64 by mutableStateOf<String?>(null)

    fun fetchContacto(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.getContactoById(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    contacto = body
                    body?.let {
                        nombre = it.nombre ?: ""
                        telefono = it.telefono ?: ""
                        direccion = it.direccion ?: ""
                        latitud = it.latitud
                        longitud = it.longitud
                        imagenBase64 = it.imagenBase64
                        firmaBase64 = it.firmaBase64
                    }
                } else {
                    errorMessage = "Error al obtener detalle: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun onImageSelected(inputStream: InputStream?) {
        viewModelScope.launch {
            inputStream?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                imagenBase64 = encodeImageToBase64(bitmap)
            }
        }
    }

    fun onPhotoTaken(bitmap: Bitmap) {
        imagenBase64 = encodeImageToBase64(bitmap)
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800 * bitmap.height / bitmap.width, true)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun actualizarContacto() {
        val current = contacto ?: return
        viewModelScope.launch {
            isUpdating = true
            updateSuccess = false
            try {
                val updated = current.copy(
                    nombre = nombre,
                    telefono = telefono,
                    direccion = direccion,
                    latitud = latitud,
                    longitud = longitud,
                    imagenBase64 = imagenBase64,
                    firmaBase64 = firmaBase64
                )
                val response = RetrofitClient.apiService.putContacto(updated)
                if (response.isSuccessful) {
                    updateSuccess = true
                    contacto = updated
                } else {
                    errorMessage = "Error al actualizar: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isUpdating = false
            }
        }
    }
}
