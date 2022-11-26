package com.example.domain.usecase

import com.example.data.dto.IdsServer
import com.example.domain.usecase.decks.*

data class DeckUsecases(
    val createDeck: CreateDeck,
    val getDecksByClientIds: GetDecksByClientIds,
    val updateDeck: UpdateDeck,
    val checkLegality: CheckLegality,
    val deleteDeck: DeleteDeck
)

sealed class DeckResult {

    data class Success(val idsServer: IdsServer) : DeckResult()
    object NameIssue : DeckResult()
    object CardAmountIssue : DeckResult()
    object ImageIssue : DeckResult()
    object ServerProblem : DeckResult()
}