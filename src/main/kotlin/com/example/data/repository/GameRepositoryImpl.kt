package com.example.data.repository

import com.example.data.dto.Game
import com.example.domain.repository.GameRepository
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class GameRepositoryImpl(db: CoroutineDatabase): GameRepository {
    private val col = db.getCollection<Game>("games")

    override suspend fun createGame(game: Game): Boolean {

        return col.insertOne(game).wasAcknowledged()
    }

    override suspend fun getGamesByOwner(owner: String): List<Game> {

        return col.find(Game::owner eq owner).toList()
    }

    override suspend fun getGameById(gameId: String): Game? {

        return col.findOneById(gameId)
    }
}