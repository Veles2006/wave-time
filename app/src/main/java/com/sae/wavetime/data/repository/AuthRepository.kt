package com.sae.wavetime.data.repository

import com.sae.wavetime.data.model.api.AuthResponse
import com.sae.wavetime.data.model.api.LoginRequest
import com.sae.wavetime.data.model.api.UserResponse
import com.sae.wavetime.network.RetrofitClient

class AuthRepository {

    suspend fun login(emailOrUserName: String, password: String): AuthResponse {
        return AuthResponse(
            message = "123",
            user = UserResponse(
                id = "1",
                username = "1",
                email = "123"
            ),
            token = "123"
        )
    }
}