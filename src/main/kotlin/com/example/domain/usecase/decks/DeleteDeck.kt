package com.example.domain.usecase.decks

import com.example.domain.repository.DecksRepository

class DeleteDeck(
    private val repository: DecksRepository
) {

    suspend operator fun invoke(deckId: String, username: String): Boolean {

        val realAuthor = repository.getAuthorById(deckId)
        if (realAuthor != username) return false

        return repository.deleteDeck(deckId)
    }
}