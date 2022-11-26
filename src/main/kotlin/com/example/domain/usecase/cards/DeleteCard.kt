package com.example.domain.usecase.cards

import com.example.domain.repository.CardsRepository
import com.example.domain.usecase.DeckUsecases

class DeleteCard(
    private val repository: CardsRepository,
    private val deckUsecases: DeckUsecases,
) {

    suspend operator fun invoke(cardId: String): Boolean {

        return repository.deleteCard(cardId)
    }
}