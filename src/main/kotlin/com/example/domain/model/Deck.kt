package com.example.domain.model

import com.example.data.dto.DeckUpdatedNonImage
import com.example.data.dto.UpdatedImage
import com.google.gson.annotations.SerializedName

//Format when returning decks to client based on how up-to-date client's cache is
data class DecksDataPackage(
    val newDecks: List<NewDeck>,
    val updatedImages: List<UpdatedImage>,
    val updatedNonImages: List<DeckUpdatedNonImage>,
    val decksToDelete: List<String>
)

//Format when returning a completely new deck
data class NewDeck(
    @SerializedName("id")
    val _id: String,
    val idForImage: String,
    val idForNonImage: String,
    val name: String,
    @SerializedName("image")
    val base64Image: String,
    val author: String,
    @SerializedName("cardList")
    val cardIds: List<String>,
)

//Format when receiving completely new deck
data class DeckFromClient(
    val deckId: String = "",
    val name: String,
    val base64Image: String,
    val cardIds: List<String>
)
