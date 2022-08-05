package com.example.plugins

import com.example.domain.model.SessionClient
import com.example.domain.model.SessionServer
import com.example.endpoints.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

fun Application.configureRouting() {

    routing {

        registration()
        authentication()



        authenticate("access") {
            friends()
            cards()
            testRoute()
        }


        get("/testroute") {
            val sessionServer = SessionServer(username = "Testaaja")
            val sessionClient = SessionClient(sessionServer._id.toString())
            println(sessionServer)
            println(sessionClient)
            call.sessions.set(sessionClient)
            call.respond("Homma ok")
        }

    }
}