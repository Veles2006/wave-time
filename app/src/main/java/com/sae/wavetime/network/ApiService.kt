package com.sae.wavetime.network

import com.sae.wavetime.data.model.api.AuthResponse
import com.sae.wavetime.data.model.api.LoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(
        @Body data: LoginRequest
    ): AuthResponse
}