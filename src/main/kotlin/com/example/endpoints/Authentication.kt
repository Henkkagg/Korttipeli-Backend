package com.example.endpoints

import com.example.domain.logicflow.Authentication
import com.example.domain.model.Credidentials
import com.example.domain.usecase.AccountManagementResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authentication() {
    val authentication = Authentication()

    post("/authentication/login") {
        val credidentials = call.receive<Credidentials>()

        val authenticationResult = authentication.checkCredidentials(credidentials)
        when (authenticationResult) {

            is AccountManagementResult.Success -> {
                val tokenPair = authentication.generateTokenPairByCredidentials(credidentials)
                if (tokenPair == null) {
                    call.respond(HttpStatusCode.ExpectationFailed)
                    return@post
                }

                call.respond(
                    HttpStatusCode.OK, hashMapOf(
                        "accessToken" to tokenPair.accessToken, "refreshToken" to tokenPair.refreshToken
                    )
                )
            }
            is AccountManagementResult.PasswordWrong -> {
                call.respond(HttpStatusCode.NonAuthoritativeInformation)
            }
            is AccountManagementResult.UsernameNotFound -> {
                call.respond(HttpStatusCode.NonAuthoritativeInformation)
            }
            else -> {
                call.respond(HttpStatusCode.ExpectationFailed)
            }
        }
    }

    authenticate("refresh") {

        post("/authentication/refresh") {
            val principal = call.principal<JWTPrincipal>()?.payload
            val id = principal?.getClaim("tokenId").toString()
            val username = principal?.getClaim("username")?.asString() ?: ""

            val tokenPair = authentication.generateTokenPairByRefresh(id, username)
            if (tokenPair == null) {
                call.respond(HttpStatusCode.NonAuthoritativeInformation)
            } else {
                call.respond(
                    HttpStatusCode.OK, hashMapOf(
                        "accessToken" to tokenPair.accessToken, "refreshToken" to tokenPair.refreshToken
                    )
                )
            }
        }
    }
}