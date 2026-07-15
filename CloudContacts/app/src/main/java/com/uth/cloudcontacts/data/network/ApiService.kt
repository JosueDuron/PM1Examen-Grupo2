package com.uth.cloudcontacts.data.network

import com.uth.cloudcontacts.data.network.model.AuthRequest
import com.uth.cloudcontacts.data.network.model.AuthResponse
import com.uth.cloudcontacts.data.network.model.ContactoRequest
import com.uth.cloudcontacts.data.network.model.ContactoResponse
import com.uth.cloudcontacts.data.network.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/Usuarios/login")
    suspend fun postLogin(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/Usuarios/registrar")
    suspend fun postRegister(@Body request: RegisterRequest): Response<Void>

    @POST("api/Contactos")
    suspend fun postContacto(
        @Body request: ContactoRequest
    ): Response<ContactoResponse>

    @GET("api/Contactos")
    suspend fun getContactosPorUsuario(
        @Query("usuarioId") usuarioId: Int
    ): Response<List<ContactoResponse>>

    @GET("api/Contactos/{id}")
    suspend fun getContactoById(@Path("id") id: Int): Response<ContactoResponse>

    @PUT("api/Contactos")
    suspend fun putContacto(@Body request: ContactoResponse): Response<Void>
}
