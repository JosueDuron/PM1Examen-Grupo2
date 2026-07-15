package com.uth.cloudcontacts.data.network.model

import com.google.gson.annotations.SerializedName

data class ContactoResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("direccion") val direccion: String?,
    @SerializedName("latitud") val latitud: Double,
    @SerializedName("longitud") val longitud: Double,
    @SerializedName("imagenBase64") val imagenBase64: String?,
    @SerializedName("firmaBase64") val firmaBase64: String?,
    @SerializedName("usuarioId") val usuarioId: Int
)
