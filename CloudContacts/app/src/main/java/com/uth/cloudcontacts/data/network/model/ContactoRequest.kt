package com.uth.cloudcontacts.data.network.model

import com.google.gson.annotations.SerializedName

data class ContactoRequest(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("latitud") val latitud: Double,
    @SerializedName("longitud") val longitud: Double,
    @SerializedName("firmaBase64") val firmaBase64: String? = null,
    @SerializedName("imagenBase64") val imagenBase64: String? = null,
    @SerializedName("usuarioId") val usuarioId: Int
)