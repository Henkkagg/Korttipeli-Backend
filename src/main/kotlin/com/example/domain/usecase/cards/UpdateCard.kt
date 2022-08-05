package com.example.domain.usecase.cards

import com.example.data.model.Card
import com.example.data.model.CardUpdatedInfo
import com.example.data.model.CardUpdatedInfoAndImage
import com.example.domain.model.NewCardInfo
import com.example.domain.repository.CardsRepository
import com.example.domain.usecase.CardResult

class UpdateCard(private val repository: CardsRepository) {

    suspend operator fun invoke(cardInfo: NewCardInfo): CardResult {

        if (cardInfo.imageId != "") {
            val base64Image = repository.getTempImage(cardInfo.imageId) ?: return CardResult.ServerError
            val card = CardUpdatedInfoAndImage(
                _id = cardInfo.id,
                title = cardInfo.title,
                description = cardInfo.description,
                type = cardInfo.type,
                base64Image = base64Image
            )
            return if (repository.updateInfoAndImage(card)) CardResult.Success(
                card._id,
                card.idForImage,
                card.idForOtherThanImage
            ) else CardResult.ServerError
        }

        val card = CardUpdatedInfo(
            _id = cardInfo.id,
            title = cardInfo.title,
            description = cardInfo.description,
            type = cardInfo.type
        )

        return if (repository.updateInfo(card)) CardResult.Success(
            id = card._id, idForOtherThanImage = card.idForOtherThanImage
        ) else CardResult.ServerError
    }
}