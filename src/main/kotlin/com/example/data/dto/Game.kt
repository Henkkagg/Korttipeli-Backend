package com.example.data.dto

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.litote.kmongo.newId

data class Game(
    @SerializedName("id")
    val _id: String = newId<Game>().toString(),
    val deckId: String,
    val name: String,
    val owner: String,
    //0 = in lobby, 1 = waiting card reveal, 2 = waiting end of turn, 3 = game has ended
    var state: Int = 0,
    val players: MutableList<String> = mutableListOf(),
    var playerInTurn: String = "",
    var cardsIdsRemaining: List<String> = emptyList(),
    var virusInPlay: VirusInplay? = null,
    var secretsInPlay: List<SecretInPlay> = emptyList()
) {
    fun toJson(): String = Gson().toJson(this)
}

data class VirusInplay(
    val cardId: String,
    val victim: String
)

data class SecretInPlay(
    val cardId: String,
    val holder: String
)
