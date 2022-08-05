package com.example.data.repository

import com.example.data.model.RefreshIdUsernamePair
import com.example.domain.repository.AuthenticationRepository
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class AuthenticationRepositoryImpl(db: CoroutineDatabase): AuthenticationRepository {
    private val col = db.getCollection<RefreshIdUsernamePair>("refresh_tokens")

    override suspend fun findUsernameAndDeleteById(id: String): String? {
        val refreshIdUsernamePair = col.findOneAndDelete("{_id: $id}")

        return refreshIdUsernamePair?.username
    }

    override suspend fun deleteByUsername(username: String): Boolean {

        return col.deleteOne(RefreshIdUsernamePair::username eq username).wasAcknowledged()
    }

    override suspend fun create(refreshIdUsernamePair: RefreshIdUsernamePair): Boolean {

        return col.insertOne(refreshIdUsernamePair).wasAcknowledged()
    }
}