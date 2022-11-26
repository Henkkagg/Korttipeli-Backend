package com.example.domain.usecase.decks

import com.example.data.dto.Deck
import com.example.data.dto.IdsServer
import com.example.domain.Util
import com.example.domain.cleanName
import com.example.domain.model.DeckFromClient
import com.example.domain.repository.CardsRepository
import com.example.domain.repository.DecksRepository
import com.example.domain.usecase.DeckResult

class CreateDeck(
    private val decksRepository: DecksRepository,
    private val cardsRepository: CardsRepository,
    private val checkLegality: CheckLegality,
) {

    suspend operator fun invoke(deckFromClient: DeckFromClient, username: String): DeckResult {
        val authorSet = hashSetOf<String>()
        authorSet.add(username)

        deckFromClient.cardIds.forEach {
            authorSet.add(cardsRepository.getAuthorById(it))
        }

        val deckInDbFormat = Deck(
            name = deckFromClient.name.cleanName(),
            author = username,
            base64Image = deckFromClient.base64Image,
            cardIds = deckFromClient.cardIds,
        )
        val legalityResult = checkLegality(deckInDbFormat)
        if (legalityResult !is DeckResult.Success) return legalityResult

        val idsServer = IdsServer(
            _id = deckInDbFormat._id,
            idForImage = deckInDbFormat.idForImage,
            idForNonImage = deckInDbFormat.idForNonImage
        )

        val success = decksRepository.createDeck(deckInDbFormat)

        if (success) cardsRepository.addDeckIdToCards(deckInDbFormat._id, deckFromClient.cardIds)
        return if (success) DeckResult.Success(idsServer) else DeckResult.ServerProblem
    }
}