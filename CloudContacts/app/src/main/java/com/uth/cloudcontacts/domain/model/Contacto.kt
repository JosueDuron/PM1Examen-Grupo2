package com.uth.cloudcontacts.domain.model

data class Contacto(
    val id: Int = 0,
    val nombre: String,
    val telefono: String,
    val latitud: Double,
    val longitud: Double,
    val imagenBase64: String?,
    val usuarioId: Int
)
