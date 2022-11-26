package com.example.data.repository

import com.example.data.dto.AccountServer
import com.example.domain.model.HashPairedWithSalt
import com.example.domain.repository.AccountRepository
import org.bson.types.Binary
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.projection
import org.litote.kmongo.eq
import org.litote.kmongo.include
import org.litote.kmongo.setValue

class AccountRepositoryImpl(database: CoroutineDatabase) : AccountRepository {
    private val col = database.getCollection<AccountServer>("users")

    override suspend fun createNew(account: AccountServer): Boolean {

        return col.insertOne(account).wasAcknowledged()
    }

    override suspend fun checkIfUsernameExists(username: String): Boolean {

        val result = col.findOne(AccountServer::username eq username)
        return result != null
    }

    override suspend fun deleteById(id: Id<AccountServer>): Boolean {

        return col.deleteOne(AccountServer::_id eq id).wasAcknowledged()
    }

    override suspend fun getHashSaltPairById(id: Id<AccountServer>): HashPairedWithSalt? {
        val projection = include(AccountServer::hash, AccountServer::salt)

        //Mongo auto converts bytearrays into binaries so they have to be retrieved as such and converted back
        data class HashPairedWithSaltAsBinary(val password: Binary, val salt: Binary)

        val result = col.withDocumentClass<HashPairedWithSaltAsBinary>().find(AccountServer::_id eq id)
            .projection(projection).first()

        val password = result?.password?.data
        val salt = result?.salt?.data
        if (password == null || salt == null) return null

        return HashPairedWithSalt(password, salt)
    }

    override suspend fun getHashSaltPairByUsername(username: String): HashPairedWithSalt? {
        val projection = include(AccountServer::hash, AccountServer::salt)

        //Mongo auto converts bytearrays into binaries so they have to be retrieved as such and converted back
        data class HashPairedWithSaltAsBinary(val hash: Binary, val salt: Binary)

        val testi = col.findOne(AccountServer::username eq username)

        val result = col.withDocumentClass<HashPairedWithSaltAsBinary>().find(AccountServer::username eq username)
            .projection(projection).first() ?: return null

        val hash = result.hash.data
        val salt = result.salt.data

        return HashPairedWithSalt(hash, salt)
    }

    override suspend fun registerToGame(gameId: String, username: String): Boolean {

        return col.updateOne(AccountServer::username eq username, setValue(AccountServer::gameId, gameId))
            .wasAcknowledged()
    }

    override suspend fun getGameIdForUser(username: String): String? {

        return col.projection(AccountServer::gameId).filter(AccountServer::username eq username).first()
    }

    override suspend fun removeGameIdForUser(username: String): Boolean {

        return col.updateOne(AccountServer::username eq username, setValue(AccountServer::gameId, null))
            .wasAcknowledged()
    }
}