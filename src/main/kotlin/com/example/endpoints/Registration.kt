package com.example.endpoints

import com.example.domain.logicflow.Registration
import com.example.domain.model.AccountClient
import com.example.domain.usecase.AccountManagementResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registration() {
    post("/registration") {
        val accountClient = call.receive<AccountClient>()

        val registrationResult = Registration().execute(accountClient)
        when (registrationResult) {

            is AccountManagementResult.Success -> {
                call.respond(HttpStatusCode.OK)
            }
            is AccountManagementResult.UsernameTaken -> {
                call.respond(HttpStatusCode.Conflict)
            }
            is AccountManagementResult.IllegalCharacters -> {
                call.respond(HttpStatusCode.BadRequest)
            }
            else -> {
                call.respond(HttpStatusCode.ExpectationFailed)
            }
        }
    }
}