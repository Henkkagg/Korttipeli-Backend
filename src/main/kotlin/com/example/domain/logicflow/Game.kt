package com.example.domain.logicflow

import com.example.data.dto.Game
import com.example.domain.cleanName
import com.example.domain.repository.CardsRepository
import com.example.domain.repository.DecksRepository
import com.example.domain.usecase.FriendlistUsecases
import com.example.endpoints.PlayerConnection
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.koin.java.KoinJavaComponent.inject
import java.util.concurrent.ConcurrentHashMap

class Game {

    private val friendlistUsecases: FriendlistUsecases by inject(FriendlistUsecases::class.java)
    private val decksRepository: DecksRepository by inject(DecksRepository::class.java)
    private val cardsRepository: CardsRepository by inject(CardsRepository::class.java)

    suspend fun createNew(
        session: DefaultWebSocketServerSession,
        gameSessions: ConcurrentHashMap<String, Pair<Game, MutableList<PlayerConnection>>>,
        username: String
    ): String? {
        val deckId = session.call.parameters["deckId"] ?: return null
        val name = session.call.parameters["name"]?.cleanName() ?: return null

        val game = Game(
            deckId = deckId,
            name = name,
            owner = username,
            players = mutableListOf(username),
            cardsIdsRemaining = decksRepository.getCards(deckId)
        )

        val connections = mutableListOf(
            PlayerConnection(
                username = username,
                webSocket = session
            )
        )
        gameSessions[game._id] = Pair(game, connections)

        broadcastGame(game, connections)

        return game._id
    }

    suspend fun joinGame(
        session: DefaultWebSocketServerSession,
        gameSessions: ConcurrentHashMap<String, Pair<Game, MutableList<PlayerConnection>>>,
        username: String,
        gameIdFromClient: String
    ): String? {

        gameSessions.forEach {
            val storedGameId = it.key
            val game = it.value.first
            val connections = it.value.second


            if (storedGameId == gameIdFromClient) {
                connections.add(
                    PlayerConnection(
                        username = username,
                        session
                    )
                )
                if (!game.players.contains(username)) {

                    game.players.add(username)
                    broadcastGame(game, connections)
                }

                return storedGameId
            }
        }
        return null
    }

    suspend fun fetchGames(
        gameSessions: ConcurrentHashMap<String, Pair<Game, MutableList<PlayerConnection>>>,
        username: String
    ): List<Game> {
        val friends = friendlistUsecases.getFriendlist(username) + username

        val games = mutableListOf<Game>()
        gameSessions.forEach { (_, pair) ->
            val game = pair.first

            if (game.state == 0 && friends.contains(game.owner)) {
                val requiredFriends = cardsRepository.getAuthorsByIds(game.cardsIdsRemaining)
                if (friends.containsAll(requiredFriends)) games.add(game)
            }
        }

        return games
    }

    fun getGame(
        gameId: String,
        gameSessions: ConcurrentHashMap<String, Pair<Game, MutableList<PlayerConnection>>>
    ): Game {

        return gameSessions[gameId]!!.first
    }

    suspend fun disconnectSession(
        gameId: String,
        gameSessions: ConcurrentHashMap<String, Pair<Game, MutableList<PlayerConnection>>>,
        session: DefaultWebSocketServerSession
    ) {
        val game = gameSessions[gameId]?.first ?: return
        val connections = gameSessions[gameId]?.second ?: return

        connections.removeIf {
            it.webSocket == session
        }

        val distinctPlayersConnected = connections.map { it.username }.distinct()

        if (game.players.count() != distinctPlayersConnected.count()) {

            game.players.removeIf { !distinctPlayersConnected.contains(it) }
            broadcastGame(game, connections)
        }
    }

    private suspend fun broadcastGame(
        game: Game,
        connections: List<PlayerConnection>
    ) {
        connections.forEach {
            it.webSocket.send(game.toJson())
        }
    }
}