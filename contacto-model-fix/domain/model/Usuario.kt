package com.uth.cloudcontacts.domain.model

data class Usuario(
    val id: Int,
    val email: String,
    val fechaRegistro: String,
    val status: Int = 1
)
