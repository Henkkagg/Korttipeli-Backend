package com.example.endpoints

import com.example.data.dto.Game
import com.example.domain.model.NewGameFromClient
import com.example.domain.usecase.GameUsecases
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

private fun Game.toJson() = Gson().toJson(this)

fun Route.game(gameUsecases: GameUsecases) {

    //data class Connection(val username: String, val session: WebSocketSession)

    val connections = ConcurrentHashMap<String, Pair<Game, MutableList<WebSocketSession>>>()

    post("/game/create") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@post

        val newGameFromClient = call.receive<NewGameFromClient>()

        val gameIdOrNull = gameUsecases.createGame(newGameFromClient, username)

        if (gameIdOrNull != null) {
            call.respond(HttpStatusCode.OK, gameIdOrNull)

            connections[gameIdOrNull] = Pair(
                Game(
                    _id = gameIdOrNull,
                    deckId = newGameFromClient.deckId,
                    name = newGameFromClient.name,
                    owner = username
                ),
                mutableListOf()
            )
        } else {
            call.respond(HttpStatusCode.ExpectationFailed)
        }
    }

    post("/game/join") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@post

        val gameId = call.receiveText()

        val gameIdOrNull = gameUsecases.joinGame(gameId, username)
        if (gameIdOrNull != null) {
            call.respond(HttpStatusCode.OK, gameIdOrNull)
        } else {
            call.respond(HttpStatusCode.ExpectationFailed)
        }
    }

    webSocket("/game/join/{gameId}") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@webSocket
        val gameId = call.parameters["gameId"]

        //If game doesn't exist yet, create it and the user becomes the admin
        if (!connections.contains(gameId)) {
            val name = call.request.queryParameters["name"] ?: return@webSocket
            val deckId = call.request.queryParameters["deckId"] ?: return@webSocket

            val newGame = Game(
                deckId = deckId,

            )
        }
        //Handle joining to game and broadcasting the new player to all connections


        launch {
            incoming.consumeAsFlow().filterIsInstance<Frame.Text>().collect {

            }
        }


        val testGame = Game(players = listOf("Jani", "Patte", "Hermanni", "Liisa-Maria"), owner = "Henkka")

        for (i in 1..100) {
            outgoing.send(Frame.Text(Gson().toJson(testGame)))
        }

        val testi: WebSocketSession = this

    }
}