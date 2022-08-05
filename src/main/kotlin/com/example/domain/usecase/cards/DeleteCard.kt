package com.example.domain.usecase.cards

import com.example.domain.repository.CardsRepository

class DeleteCard (private val repository: CardsRepository) {

    suspend operator fun invoke(cardId: String): Boolean {

        return repository.deleteCard(cardId)
    }
}