package com.uth.cloudcontacts.ui.viewmodels

import android.app.Application
import android.content.Context
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
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AgregarContactoViewModel(application: Application) : AndroidViewModel(application) {
    var nombre by mutableStateOf("")
    var telefono by mutableStateOf("")
    var direccion by mutableStateOf("")
    var latitud by mutableStateOf(0.0)
    var longitud by mutableStateOf(0.0)
    var firmaBase64 by mutableStateOf("")
    var fotoBase64 by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onTelefonoChange(newValue: String) {
        val digitsOnly = newValue.filter { it.isDigit() }
        telefono = when {
            digitsOnly.length <= 4 -> digitsOnly
            digitsOnly.length <= 8 -> "${digitsOnly.substring(0, 4)}-${digitsOnly.substring(4)}"
            else -> "${digitsOnly.substring(0, 4)}-${digitsOnly.substring(4, 8)}"
        }
    }

    fun onImageSelected(context: Context, inputStream: InputStream?) {
        inputStream?.let {
            val bitmap = BitmapFactory.decodeStream(it)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val bytes = outputStream.toByteArray()
            fotoBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }

    fun guardarContacto(usuarioId: Int) {
        if (nombre.isBlank() || telefono.isBlank() || direccion.isBlank()) {
            errorMessage = "Complete todos los campos obligatorios"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            isSuccess = false
            try {
                val request = ContactoRequest(
                    nombre = nombre,
                    telefono = telefono,
                    direccion = direccion,
                    latitud = latitud,
                    longitud = longitud,
                    firmaBase64 = firmaBase64,
                    imagenBase64 = fotoBase64,
                    usuarioId = usuarioId
                )

                val response = RetrofitClient.getApiService(getApplication())
                    .crearContacto(request)

                if (response.isSuccessful) {
                    isSuccess = true
                    resetFields()
                } else {
                    errorMessage = "Error al guardar: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun resetFields() {
        nombre = ""
        telefono = ""
        direccion = ""
        latitud = 0.0
        longitud = 0.0
        firmaBase64 = ""
        fotoBase64 = ""
    }
}
