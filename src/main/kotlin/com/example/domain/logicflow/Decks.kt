package com.example.domain.logicflow

import com.example.data.dto.IdsServer
import com.example.domain.model.DecksDataPackage
import com.example.domain.model.DeckFromClient
import com.example.domain.usecase.DeckResult
import com.example.domain.usecase.DeckUsecases
import org.koin.java.KoinJavaComponent.inject

class Decks {
    private val deckUsecases: DeckUsecases by inject(DeckUsecases::class.java)

    suspend fun createDeck(deckFromClient: DeckFromClient, username: String): DeckResult {

        return deckUsecases.createDeck(deckFromClient, username)
    }

    suspend fun updateDeck(deckFromClient: DeckFromClient, username: String): DeckResult {

        return deckUsecases.updateDeck(deckFromClient, username)
    }

    suspend fun getDecks(idsList: List<IdsServer>, username: String): DecksDataPackage {

        return deckUsecases.getDecksByClientIds(idsList, username)
    }

    suspend fun deleteDeck(deckId: String, username: String): Boolean {

        return deckUsecases.deleteDeck(deckId, username)
    }
}