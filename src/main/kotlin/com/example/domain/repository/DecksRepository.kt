package com.example.domain.repository

import com.example.data.dto.*
import com.example.domain.model.NewDeck

interface DecksRepository {

    suspend fun createDeck(deck: Deck): Boolean
    suspend fun getNewDecks(ids: List<String>): List<NewDeck>

    suspend fun getDecksByAuthors(authors: List<String>): List<String>
    suspend fun getAuthorById(deckId: String): String
    suspend fun getImages(ids: List<String>): List<UpdatedImage>
    suspend fun getOtherThanImages(ids: List<String>): List<DeckUpdatedNonImage>

    suspend fun updateDeck(deck: Deck): Boolean

    //Only used when card has been removed from deck
    suspend fun updateCards(deckId: String, cardIds: List<String>): Boolean

    suspend fun getCards(deckId: String): List<String>

    suspend fun getIdsByIds(deckIds: List<String>): List<IdsServer>

    suspend fun deleteDeck(deckId: String): Boolean
}