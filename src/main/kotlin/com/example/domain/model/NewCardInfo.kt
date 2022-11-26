package com.example.domain.model

import com.example.data.dto.Card
import com.example.data.dto.UpdatedImage
import com.example.data.dto.UpdatedInfo

//Information that client sends to server when creating or updating card. Image is sent separately
data class NewCardInfo(
    val id: String = "",
    val title: String,
    val description: String,
    //1=action, 2=virus, 3=secret
    val type: Int,
    //imageId will be empty
    val imageId: String
)

//Sent to client when getting request for cards
data class CardDataPackage(
    val cardsToDelete: List<String>,
    val newCards: List<Card>,
    val updatedInfos: List<UpdatedInfo>,
    val updatedImages: List<UpdatedImage>
)
