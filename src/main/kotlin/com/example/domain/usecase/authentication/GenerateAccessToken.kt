package com.example.domain.usecase.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class GenerateAccessToken {
    operator fun invoke(username: String): String {

        val expiresIn: Long = 300000
        val audience: String = System.getenv("JWT_audience")
        val issuer: String = System.getenv("JWT_issuer")
        val secret: String = System.getenv("JWT_secret")

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
            .withClaim("username", username)


        return token.sign(Algorithm.HMAC256(secret))
    }
}