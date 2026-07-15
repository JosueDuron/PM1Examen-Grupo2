package com.uth.cloudcontacts.data.network.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String?,
    @SerializedName("mensaje") val mensaje: String?
)
