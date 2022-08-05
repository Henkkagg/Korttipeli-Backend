package com.example.domain.usecase.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.RefreshIdUsernamePair
import org.litote.kmongo.Id
import java.util.*

class GenerateRefreshToken {
    operator fun invoke(tokenId: String, username: String): String {

        val expiresIn: Long = 3600000
        val audience: String = System.getenv("JWT_audience")
        val issuer: String = System.getenv("JWT_issuer")
        val secret: String = System.getenv("JWT_secret")

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
            .withClaim("tokenId", tokenId)
            .withClaim("username", username)


        return token.sign(Algorithm.HMAC256(secret))
    }
}