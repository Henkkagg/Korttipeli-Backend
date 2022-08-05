package com.example.security

data class TokenConfig(
    val expiresIn: Long,
    val audience: String = System.getenv("JWT_audience"),
    val issuer: String = System.getenv("JWT_issuer"),
    val secret: String = System.getenv("JWT_secret")
)
