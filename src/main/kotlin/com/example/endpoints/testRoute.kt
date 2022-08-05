package com.example.endpoints

import com.example.domain.logicflow.Cards
import de.undercouch.bson4jackson.serializers.BsonDateSerializer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.BsonDateTime
import java.util.Date

fun Route.testRoute() {

    val cards = Cards()

    post("/cards/test") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@post
        cards.getAllCards(emptyList(), username)

    }

}