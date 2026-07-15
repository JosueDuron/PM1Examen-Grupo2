package com.uth.cloudcontacts.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.cloudcontacts.data.network.RetrofitClient
import com.uth.cloudcontacts.data.network.model.ContactoRequest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AgregarContactoViewModel : ViewModel() {

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
    var fotoBase64 by mutableStateOf("")
    var firmaBase64 by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    fun onImageSelected(context: Context, inputStream: InputStream?) {
        viewModelScope.launch {
            inputStream?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                fotoBase64 = encodeImageToBase64(bitmap)
            }
        }
    }

    fun onPhotoTaken(bitmap: Bitmap) {
        fotoBase64 = encodeImageToBase64(bitmap)
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        // Reducir resolución para optimizar
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800 * bitmap.height / bitmap.width, true)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun guardarContacto(usuarioId: Int) {
        if (nombre.isBlank() || telefono.isBlank()) {
            errorMessage = "Nombre y Teléfono son obligatorios"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = ContactoRequest(
                    nombre = nombre,
                    telefono = telefono,
                    direccion = direccion,
                    latitud = latitud,
                    longitud = longitud,
                    imagenBase64 = fotoBase64,
                    firmaBase64 = firmaBase64,
                    usuarioId = usuarioId
                )
                val response = RetrofitClient.apiService.postContacto(request)
                if (response.isSuccessful) {
                    isSuccess = true
                } else {
                    errorMessage = "Error al guardar: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
