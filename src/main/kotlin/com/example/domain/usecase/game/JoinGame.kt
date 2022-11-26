package com.example.domain.usecase.game

import com.example.domain.repository.AccountRepository
import com.example.domain.repository.GameRepository

class JoinGame(
    private val gameRepository: GameRepository,
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(gameId: String, username: String): String? {

        val currentGame = accountRepository.getGameIdForUser(username)

        /*
        1. User is not registered to any game yet
        2. User has already registered to the game, and is just reconnecting
        3. User has registered to a different game, and thus can't join this at the same time (prohibited client level)

         */
        when {
            //1
            currentGame == null -> {
                accountRepository.registerToGame(gameId, username)
            }
            //2
            gameId == currentGame -> {

            }
            //3
            else -> {
                return null
            }
        }

        return gameId
    }
}