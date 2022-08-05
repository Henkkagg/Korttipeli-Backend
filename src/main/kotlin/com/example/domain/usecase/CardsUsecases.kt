package com.example.domain.usecase

import com.example.domain.usecase.cards.*

data class CardsUsecases(
    val createTempImage: CreateTempImage,
    val createCard: CreateCard,
    val getIdsByAuthors: GetIdsByAuthors,
    val getUpdatesByIds: GetUpdatesByIds,
    val getCardsByIds: GetCardsByIds,
    val verifyAuthority: VerifyAuthority,
    val verifyContentLegality: VerifyContentLegality,
    val updateCard: UpdateCard,
    val deleteCard: DeleteCard
)

sealed class CardResult {
    data class Success(
        val id: String = "", val idForImage: String = "", val idForOtherThanImage: String = "", val author: String = ""
    ) : CardResult()

    data class ImageTooLarge(val attemptedSizeInKb: Int, val allowedSizeInKb: Int) : CardResult()
    data class TitleTooLong(val attemptedLength: Int, val allowedLength: Int) : CardResult()
    data class DescriptionTooLong(val attemptedLength: Int, val allowedLength: Int) : CardResult()
    data class TitleAndDescriptionTooLong(
        val attemptedTitleLength: Int,
        val allowedTitleLength: Int,
        val attemptedDescriptionLength: Int,
        val allowedDescriptionLength: Int
    ) : CardResult()

    object ServerError : CardResult()
}