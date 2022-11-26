package com.example.domain.usecase.decks

import com.example.data.dto.Deck
import com.example.data.dto.IdsServer
import com.example.domain.Util
import com.example.domain.cleanName
import com.example.domain.model.DeckFromClient
import com.example.domain.repository.DecksRepository
import com.example.domain.usecase.DeckResult
import com.example.domain.usecase.DeckUsecases
import org.litote.kmongo.newId

class UpdateDeck(
    private val decksRepository: DecksRepository,
    private val checkLegality: CheckLegality
) {

    /*
    1. Verify that the user owns the deck
    2. Check which parts of the deck have been updated. If a field is empty, it was not modified
    3. If the values are legal, Update the deck in Mongo
    4. Return the new ids to client
     */

    suspend operator fun invoke(deckFromClient: DeckFromClient, username: String): DeckResult {

        val deckFromDb = decksRepository.getNewDecks(listOf(deckFromClient.deckId)).firstOrNull()
            ?: return DeckResult.ServerProblem

        //1. Verify that the user owns the deck
        val realAuthor = deckFromDb.author
        if (realAuthor != username) return DeckResult.ServerProblem

        //2. Check which parts of the deck have been updated. If a field is empty, it was not modified
        val imageWasModified = deckFromClient.base64Image.isNotEmpty()
        val otherThanImageWasModified =
            deckFromClient.cardIds.isNotEmpty() && deckFromClient.name.cleanName().isNotEmpty()

        val idForImage = if (imageWasModified) newId<Deck>().toString() else deckFromDb.idForImage
        val idForNonImage = if (otherThanImageWasModified) newId<Deck>().toString() else deckFromDb.idForNonImage
        val base64Image = if (imageWasModified) deckFromClient.base64Image else deckFromDb.base64Image
        val name = if (deckFromClient.name.cleanName().isNotEmpty()) deckFromClient.name else deckFromDb.name
        val cardIds = if (deckFromClient.cardIds.isNotEmpty()) deckFromClient.cardIds else deckFromDb.cardIds

        //3. If the values are legal, Update the deck in Mongo
        val updatedDeck = Deck(
            _id = deckFromDb._id,
            idForImage = idForImage,
            idForNonImage = idForNonImage,
            name = name,
            base64Image = base64Image,
            author = deckFromDb.author,
            cardIds = cardIds
        )
        val legalityResult = checkLegality(updatedDeck)
        if (legalityResult !is DeckResult.Success) return legalityResult

        val success = decksRepository.updateDeck(updatedDeck)
        if (!success) return DeckResult.ServerProblem

        //4.
        val newIds = IdsServer(
            _id = updatedDeck._id,
            idForImage = updatedDeck.idForImage,
            idForNonImage = updatedDeck.idForNonImage
        )

        return DeckResult.Success(newIds)
    }
}