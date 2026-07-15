package com.uth.cloudcontacts.data.network.model

data class ContactoRequest(
    val id: Int? = null,
    val nombre: String,
    val telefono: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val firmaBase64: String? = null,
    val imagenBase64: String? = null,
    val usuarioId: Int
)
