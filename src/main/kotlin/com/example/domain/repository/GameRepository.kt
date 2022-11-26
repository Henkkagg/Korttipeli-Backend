package com.example.domain.repository

import com.example.data.dto.Game

interface GameRepository {

    suspend fun createGame(game: Game): Boolean

    suspend fun getGamesByOwner(owner: String): List<Game>

    suspend fun getGameById(gameId: String): Game?
}