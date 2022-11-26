package com.example.domain.repository

import com.example.data.dto.AccountServer
import com.example.domain.model.HashPairedWithSalt
import org.litote.kmongo.Id

interface AccountRepository{
    suspend fun createNew(account: AccountServer): Boolean
    suspend fun checkIfUsernameExists(username: String): Boolean
    suspend fun deleteById(id: Id<AccountServer>): Boolean
    suspend fun getHashSaltPairById(id: Id<AccountServer>): HashPairedWithSalt?
    suspend fun getHashSaltPairByUsername(username: String): HashPairedWithSalt?
    suspend fun registerToGame(gameId: String, username: String): Boolean
    suspend fun getGameIdForUser(username: String): String?
    suspend fun removeGameIdForUser(username: String): Boolean
}
