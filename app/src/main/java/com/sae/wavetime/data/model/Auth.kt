package com.sae.wavetime.data.model

data class UserResponse(
    val id: String,
    val username: String,
    val email: String,
)

data class LoginRequest(
    val emailOrUsername: String,
    val password: String,
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
)

data class AuthResponse(
    val message: String,
    val user: UserResponse,
    val token: String,
)
