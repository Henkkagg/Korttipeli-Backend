package com.example.domain.usecase.cards

import com.example.data.dto.Card
import com.example.domain.model.NewCardInfo
import com.example.domain.repository.CardsRepository
import com.example.domain.usecase.CardResult

class CreateCard(private val repository: CardsRepository) {

    suspend operator fun invoke(newCardInfo: NewCardInfo, username: String): CardResult {

        val base64Image = repository.getTempImage(newCardInfo.imageId) ?: return CardResult.ServerError

        val card = Card(
            title = newCardInfo.title,
            description = newCardInfo.description,
            type = newCardInfo.type,
            author = username,
            base64Image = base64Image
        )
        val success = repository.createCard(card)

        return if (success) {
            CardResult.Success(
                id = card._id,
                idForImage = card.idForImage,
                idForOtherThanImage = card.idForNonImage,
                author = username
            )
        } else CardResult.ServerError
    }
}