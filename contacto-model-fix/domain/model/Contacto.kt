package com.uth.cloudcontacts.domain.model

data class Contacto(
    val id: Int,
    val nombre: String,
    val telefono: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val firmaBase64: String?,
    val imagenBase64: String?,
    val fechaCreacion: String,
    val usuarioId: Int,
    val status: Int = 1
)
