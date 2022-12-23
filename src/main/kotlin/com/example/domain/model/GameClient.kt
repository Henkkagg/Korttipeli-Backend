package com.example.domain.model

import com.example.data.dto.SecretInPlay
import com.example.data.dto.VirusInplay

data class GameClient(
    val _id: String,
    val deckId: String,
    val owner: String,
    //0 = in lobby, 1 = in play
    val state: Int,
    val players: List<String>,
    val playerInTurn: String,
    val cardsIdsRemaining: List<String>,
    val virusInPlay: VirusInplay,
    val secretsInPlay: List<SecretInPlay>
)

data class NewGameFromClient(
    val deckId: String,
    val name: String
)

sealed class GameInput {
    object StartGame: GameInput()
    object FinishTurn: GameInput()
    class UseSecret(val cardId: String): GameInput()
}
