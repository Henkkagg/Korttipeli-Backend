package com.example.domain.usecase.game

import com.example.data.dto.Game
import com.example.domain.repository.CardsRepository
import com.example.domain.repository.DecksRepository
import com.example.domain.usecase.FriendlistUsecases

class UpdateAuthorizedCards(
    private val decksRepository: DecksRepository,
    private val cardsRepository: CardsRepository,
    private val friendlistUsecases: FriendlistUsecases
) {

    suspend operator fun invoke(game: Game) {

        //If game is already running, don't do anything
        if (game.state == 1) return

        //Establish list of people who ALL are each other's friends
        val commonFriends = hashSetOf<String>()
        val firstPlayer = game.players.firstOrNull()
        if (firstPlayer == null) {
            println("Error: firstPlayer oli null... ????")
            return
        }
        val startingList = friendlistUsecases.getFriendlist(firstPlayer) + firstPlayer
        commonFriends.addAll(startingList)

        for (i in 1..game.players.lastIndex) {
            val currentPlayer = game.players[i]
            val friendsOfCurrent = friendlistUsecases.getFriendlist(currentPlayer)
            commonFriends.retainAll((friendsOfCurrent + currentPlayer).toSet())
        }

        //Create a new list where cards authorized to use by ALL are added
        val allCardIdsInDeck = decksRepository.getCards(game.deckId)
        val cardIdsWithAuthors = cardsRepository.getIdAuthorPairsByIds(allCardIdsInDeck)

        val authorizedCards = mutableListOf<String>()
        cardIdsWithAuthors.forEach {
            if (commonFriends.contains(it.second) && it.first != null) authorizedCards.add(it.first!!)
        }

        game.cardsIdsRemaining = authorizedCards
    }
}