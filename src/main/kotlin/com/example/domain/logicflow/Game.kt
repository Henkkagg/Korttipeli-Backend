package com.example.domain.logicflow

import com.example.data.dto.Card
import com.example.data.dto.Game
import com.example.data.dto.SecretInPlay
import com.example.data.dto.VirusInplay
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

        val cards = decksRepository.getCards(deckId).shuffled()

        val game = Game(
            deckId = deckId,
            name = name,
            owner = username,
            players = mutableListOf(username),
            playerInTurn = username,
            cardsIdsRemaining = cards + cards.first()
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
        session: DefaultWebSocketServerSession,
        username: String
    ) {
        val game = gameSessions[gameId]?.first ?: return
        val connections = gameSessions[gameId]?.second ?: return

        val userWasOwner = game.owner == username
        if (userWasOwner) {
            connections.forEach {
                it.webSocket.close(CloseReason(CloseReason.Codes.GOING_AWAY, "Game has ended"))
            }
            gameSessions.remove(gameId)
            return
        }

        connections.removeIf {
            it.webSocket == session
        }

        val distinctPlayersConnected = connections.map { it.username }.distinct()

        if (game.players.count() != distinctPlayersConnected.count()) {

            if (game.playerInTurn == username) game.playerInTurn = nextPlayer(game)

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

    suspend fun inputStartGame(
        gameSessions: ConcurrentHashMap<String, Pair<Game, MutableList<PlayerConnection>>>,
        gameId: String,
        username: String
    ) {
        val connections = gameSessions[gameId]?.second ?: return
        val game = gameSessions[gameId]?.first ?: return

        if (game.owner == username) {
            game.state = 1
            broadcastGame(game, connections)
        }

    }

    suspend fun inputRevealCard(
        gameSessions: ConcurrentHashMap<String, Pair<Game, MutableList<PlayerConnection>>>,
        gameId: String,
        username: String
    ) {
        val game = gameSessions[gameId]?.first ?: return
        val connections = gameSessions[gameId]?.second ?: return


        if (username != game.playerInTurn) return

        game.cardsIdsRemaining = game.cardsIdsRemaining.drop(1)

        val currentCard = cardsRepository.getCardsByIds(listOf(game.cardsIdsRemaining.first())).first()
        if (currentCard.type == 3) {
            game.secretsInPlay = game.secretsInPlay + SecretInPlay(currentCard._id, username)
            println("Lisätty secretti")
        }


        game.state = 2
        broadcastGame(game, connections)
    }

    suspend fun inputEndTurn(
        gameSessions: ConcurrentHashMap<String, Pair<Game, MutableList<PlayerConnection>>>,
        gameId: String,
        username: String
    ) {
        val game = gameSessions[gameId]?.first ?: return
        val connections = gameSessions[gameId]?.second ?: return

        if (game.cardsIdsRemaining.size == 1) {
            endGame(gameSessions, gameId)
            println("Yritetään lopettaa peliä")
            return
        }

        val currentCard = cardsRepository.getCardsByIds(listOf(game.cardsIdsRemaining.first())).first()

        if (username != game.playerInTurn) return

        if (currentCard.type == 2) {
            game.virusInPlay = VirusInplay(currentCard._id, username)
        }

        game.playerInTurn = nextPlayer(game)

        game.state = 1

        broadcastGame(game, connections)
    }

    private fun nextPlayer(game: Game): String {
        var playerPosition = 0
        for (i in game.players.indices) {
            playerPosition = i
            if (game.players[i] == game.playerInTurn) break
        }

        return if (game.players.lastIndex == playerPosition) {
            game.players.first()
        } else {
            game.players[playerPosition + 1]
        }
    }

    suspend fun inputUseSecret(
        gameSessions: ConcurrentHashMap<String, Pair<Game, MutableList<PlayerConnection>>>,
        gameId: String,
        username: String,
        cardId: String
    ) {
        val game = gameSessions[gameId]?.first ?: return
        val connections = gameSessions[gameId]?.second ?: return

        println("Yritetään käyttää")
        println(game.secretsInPlay)

        val secret = game.secretsInPlay.firstOrNull { it.cardId == cardId } ?: return
        if (secret.holder != username) return

        game.secretsInPlay = game.secretsInPlay.filterNot { it == secret }

        broadcastGame(game, connections)

        println("Secretti käytetty onnistuneesti")
    }

    private suspend fun endGame(
        gameSessions: ConcurrentHashMap<String, Pair<Game, MutableList<PlayerConnection>>>,
        gameId: String,
    ) {
        val game = gameSessions[gameId]?.first ?: return
        val connections = gameSessions[gameId]?.second ?: return

        game.state = 3

        broadcastGame(game, connections)
    }
}