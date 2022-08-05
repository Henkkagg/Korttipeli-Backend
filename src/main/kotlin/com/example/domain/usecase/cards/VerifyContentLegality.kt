package com.example.domain.usecase.cards

import com.example.domain.model.NewCardInfo
import com.example.domain.usecase.CardResult

class VerifyContentLegality {

    operator fun invoke(newCardInfo: NewCardInfo): CardResult {

        val attemptedTitleLength = newCardInfo.title.length
        val allowedTitleLength = 30
        val attemptedDescriptionLength = newCardInfo.description.length
        val allowedDescriptionLength = 500
        val attemptedType = newCardInfo.type
        val allowedTypes = listOf(1, 2, 3)

        if (attemptedTitleLength > allowedTitleLength) {
            return CardResult.TitleTooLong(attemptedTitleLength, allowedTitleLength)
        }
        if (attemptedDescriptionLength > allowedDescriptionLength) {
            return CardResult.DescriptionTooLong(attemptedDescriptionLength, allowedDescriptionLength)
        }
        if (!allowedTypes.any { it == attemptedType }) return CardResult.ServerError

        return CardResult.Success()
    }
}