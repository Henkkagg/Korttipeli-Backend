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
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.bson.BsonDateTime
import java.util.Date

fun Route.testRoute() {

    val cards = Cards()

    post("/cards/test") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@post
        cards.getAllCards(emptyList(), username)

    }

    webSocket("/game/testi") {

        incoming.consumeAsFlow().onEach {
            println("Viesti on: $it")
        }.launchIn(this)

        for (i in 1..100) {
            outgoing.send(Frame.Text("Moro... $i"))
            delay(1000)
            if (i == 5) close(CloseReason(1, "Se ommoro"))
        }
    }

}