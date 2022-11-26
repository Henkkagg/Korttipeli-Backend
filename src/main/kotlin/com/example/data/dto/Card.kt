package com.example.data.dto

import com.google.gson.annotations.SerializedName
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.newId

data class Card(
    @BsonId
    @SerializedName("id")
    val _id: String= newId<Card>().toString(),
    //Ids verify that the corresponding data is still up-to-date in the cached card.
    //If data has been changed, Ids help client to only update the relevant part of the card.
    val idForImage: String = newId<Card>().toString(),
    val idForNonImage: String = newId<Card>().toString(),

    val author: String,
    val title: String,
    val description: String,
    @SerializedName("image")
    val base64Image: String,
    //1=action, 2=virus, 3=secret
    val type: Int,
    val containedInDecks: List<String> = emptyList()
)

data class CardUpdatedInfoAndImage(
    val _id: String= newId<Card>().toString(),
    val idForImage: String = newId<Card>().toString(),
    val idForNonImage: String = newId<Card>().toString(),

    val title: String,
    val description: String,
    val base64Image: String,
    //1=action, 2=virus, 3=secret
    val type: Int
)

data class CardUpdatedInfo(
    val _id: String= newId<Card>().toString(),
    val idForNonImage: String = newId<Card>().toString(),

    val title: String,
    val description: String,
    //1=action, 2=virus, 3=secret
    val type: Int
)

//This is for both cards and decks
data class IdsServer(
    @SerializedName("id")
    val _id: String,
    val idForImage: String,
    val idForNonImage: String
)

//Sent to client if image has changed on existing card or deck
data class UpdatedImage(
    @SerializedName("id")
    val _id: String,
    val idForImage: String,
    @SerializedName("image")
    val base64Image: String
)

//Sent to client if other than image has changed on existing card
data class UpdatedInfo(
    @SerializedName("id")
    val _id: String,
    val idForNonImage: String,
    val title: String,
    val description: String,
    //1=action, 2=virus, 3=secret
    val type: Int
)