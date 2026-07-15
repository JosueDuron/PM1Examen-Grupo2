package com.uth.cloudcontacts.data.network.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("Email") val email: String,
    @SerializedName("Password") val password: String
)
