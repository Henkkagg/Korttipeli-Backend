package com.example.domain.usecase.game

import com.example.domain.repository.AccountRepository
import com.example.domain.repository.GameRepository

class GetGameId(
    private val gameRepository: GameRepository,
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(username: String): String? {

         return accountRepository.getGameIdForUser(username)
    }
}