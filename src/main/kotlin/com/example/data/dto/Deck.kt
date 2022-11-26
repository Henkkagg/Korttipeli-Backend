package com.example.data.dto

import com.google.gson.annotations.SerializedName
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.newId

//MongoDB format
data class Deck(
    @BsonId
    val _id: String = newId<Deck>().toString(),
    val idForImage: String = newId<Deck>().toString(),
    val idForNonImage: String = newId<Deck>().toString(),
    val name: String,
    val base64Image: String,
    val author: String,
    val cardIds: List<String>,
)

data class CardAuthor(
    val name: String,
    val occurrence: Int
)

data class DeckIdsWithCardAuthors(
    val _id: String,
    val idForImage: String = newId<Deck>().toString(),
    val idForNonImage: String = newId<Deck>().toString(),
    val cardIds: List<String>,
    val cardAuthors: List<String>
) {
    fun toIdsServer() = IdsServer(_id, idForImage, idForNonImage)
}

data class DeckUpdatedNonImage(
    @SerializedName("id")
    val _id: String,
    val idForNonImage: String,
    val name: String,
    @SerializedName("cardList")
    val cardIds: List<String>
)

//No DeckUpdatedImage - use UpdatedImage instead (same than used for cards)

//No DeckIds - use IdsServer instead (same than used for cards)
