package com.uth.cloudcontacts.data.network.model

import com.google.gson.annotations.SerializedName

data class ContactoRequest(
    @SerializedName("Nombre") val nombre: String,
    @SerializedName("Telefono") val telefono: String,
    @SerializedName("Direccion") val direccion: String,
    @SerializedName("Latitud") val latitud: Double,
    @SerializedName("Longitud") val longitud: Double,
    @SerializedName("FirmaBase64") val firmaBase64: String,
    @SerializedName("ImagenBase64") val imagenBase64: String,
    @SerializedName("UsuarioId") val usuarioId: Int
)
