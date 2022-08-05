package com.example.plugins

import com.example.domain.model.SessionClient
import io.ktor.server.sessions.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.*

fun Application.configureSecurity() {
    install(Sessions) {
        val encryptKey = hex("4d07cfc7195edaba8ecdca3b7bd887e1") //System.getenvs("EncryptKey")
        val signKey = hex("14034e91822d9626fca5c750b44b1626") //System.getenvs("SignKey")
        cookie<SessionClient>("user_session") {
            cookie.maxAgeInSeconds = 3600
            transform(SessionTransportTransformerEncrypt(encryptKey, signKey))
        }
    }

}
