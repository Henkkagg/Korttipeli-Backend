package com.example.domain.usecase.decks

import com.example.data.dto.Deck
import com.example.data.dto.IdsServer
import com.example.domain.Util
import com.example.domain.usecase.DeckResult

class CheckLegality {

    operator fun invoke(deck: Deck): DeckResult {

        if (deck.cardIds.isEmpty() || deck.cardIds.size > 200) return DeckResult.CardAmountIssue

        if (deck.name.isEmpty() || deck.name.length > 20) return DeckResult.NameIssue

        val attemptedImageSizeInKb = deck.base64Image.length / 1000
        val allowedImageSizeInKb = 500
        if (attemptedImageSizeInKb > allowedImageSizeInKb || attemptedImageSizeInKb < 1) return DeckResult.ImageIssue

        return DeckResult.Success(IdsServer("", "", ""))
    }
}