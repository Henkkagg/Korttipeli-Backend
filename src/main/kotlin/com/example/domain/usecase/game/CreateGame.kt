package com.example.domain.usecase.game

import com.example.data.dto.Game
import com.example.domain.model.NewGameFromClient
import com.example.domain.repository.AccountRepository
import com.example.domain.repository.GameRepository

class CreateGame(
    private val gameRepository: GameRepository,
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(newGameFromClient: NewGameFromClient, username: String): String? {

        //Person can have joined only one game at once, so prohibit creating new if already joined to one
        val existingGame = accountRepository.getGameIdForUser(username)
        if (existingGame != "") return null

        val newGame = Game(
            deckId = newGameFromClient.deckId,
            name = newGameFromClient.name,
            owner = username
        )

        val newGameWasCreated = gameRepository.createGame(newGame)
        if (newGameWasCreated) accountRepository.registerToGame(newGame._id, username)

        return newGame._id
    }
}