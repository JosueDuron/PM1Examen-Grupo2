package com.uth.cloudcontacts.data.network

import com.uth.cloudcontacts.data.network.model.*
import com.uth.cloudcontacts.domain.model.Contacto
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/usuarios/registrar")
    suspend fun registrarUsuario(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/usuarios/login")
    suspend fun loginUsuario(@Body request: AuthRequest): Response<AuthResponse>

    @GET("api/contactos")
    suspend fun obtenerContactos(@Query("usuarioId") usuarioId: Int): Response<List<Contacto>>

    @GET("api/contactos/{id}")
    suspend fun obtenerContactoPorId(@Path("id") id: Int): Response<Contacto>

    @POST("api/contactos")
    suspend fun crearContacto(@Body request: ContactoRequest): Response<Contacto>

    @PUT("api/contactos")
    suspend fun actualizarContacto(@Body request: ContactoRequest): Response<Unit>

    @DELETE("api/contactos/{id}")
    suspend fun eliminarContacto(@Path("id") id: Int): Response<Unit>

    @PUT("api/usuarios")
    suspend fun actualizarUsuario(@Body request: ActualizarUsuarioDto): Response<Unit>

    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Int): Response<Unit>

    @GET("api/contactos/buscar")
    suspend fun buscarContactos(
        @Query("texto") texto: String,
        @Query("usuarioId") usuarioId: Int
    ): Response<List<Contacto>>
}
