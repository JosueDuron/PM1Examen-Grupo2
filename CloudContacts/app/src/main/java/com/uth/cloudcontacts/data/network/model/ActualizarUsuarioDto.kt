package com.uth.cloudcontacts.data.network.model

import com.google.gson.annotations.SerializedName

data class ActualizarUsuarioDto(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("status") val status: Int
)
