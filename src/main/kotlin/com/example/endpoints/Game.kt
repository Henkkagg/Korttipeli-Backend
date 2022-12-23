package com.example.endpoints

import com.example.data.dto.Game
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

data class PlayerConnection(
    val username: String,
    val webSocket: WebSocketSession
)

fun Route.game(gameLogic: com.example.domain.logicflow.Game) {

    val gameSessions = ConcurrentHashMap<String, Pair<Game, MutableList<PlayerConnection>>>()

    get("/game/fetch") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@get

        val games = gameLogic.fetchGames(gameSessions, username)

        call.respond(games)
    }

    webSocket("/game/join/{gameId}") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@webSocket

        val gameIdOrEmptyIfNewGame = call.parameters["gameId"]?.drop(1) ?: "?"
        val creatingNew = gameIdOrEmptyIfNewGame == ""

        val verifiedGameId = if (creatingNew) {
            gameLogic.createNew(this, gameSessions, username)
        } else {
            gameLogic.joinGame(this, gameSessions, username, gameIdOrEmptyIfNewGame)
        }

        if (verifiedGameId == null) {
            println("$username yritti liittyä peliiin $gameIdOrEmptyIfNewGame tai luoda uuden. Epäonnistui")
            close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Peliin ei voinut liittyä"))
            return@webSocket
        }

        for (frame in incoming) {
            if (frame !is Frame.Text) continue

            println(frame.readText())
        }

        gameLogic.disconnectSession(verifiedGameId, gameSessions, this)

    }
}