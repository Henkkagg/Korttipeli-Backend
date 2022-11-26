package com.example.data.dto

import com.google.gson.annotations.SerializedName
import org.litote.kmongo.newId

data class Game(
    @SerializedName("id")
    val _id: String = newId<Game>().toString(),
    val deckId: String = "iidee123445",
    val name: String = "Testipeli tervetuloa",
    val owner: String = "Testiuseri",
    //0 = in lobby, 1 = in play
    var state: Int = 0,
    var players: List<String> = emptyList(),
    var playerInTurn: String = "",
    var cardsIdsRemaining: List<String> = emptyList(),
    var virusInPlay: VirusInplay? = null,
    var secretsInPlay: List<SecretInPlay> = emptyList()
) {

}

data class VirusInplay(
    val cardId: String,
    val victim: String
)

data class SecretInPlay(
    val cardId: String,
    val holder: String
)
