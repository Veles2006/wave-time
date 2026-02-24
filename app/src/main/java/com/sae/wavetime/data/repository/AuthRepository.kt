package com.sae.wavetime.data.repository

import com.sae.wavetime.data.model.AuthResponse
import com.sae.wavetime.data.model.UserResponse

class AuthRepository {

    suspend fun login(emailOrUserName: String, password: String): AuthResponse {
        return AuthResponse(
            "abc",
            UserResponse("diwi", "veles", "anhphia@gmail.com"),
            "iahiweifivn"
        )
    }
}