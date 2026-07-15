package com.uth.cloudcontacts.data.network.model

data class AuthResponse(
    val id: Int,
    val email: String,
    val token: String,
    val mensaje: String
)
