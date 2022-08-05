package com.example.domain.repository

import com.example.data.model.AccountServer
import com.example.domain.model.HashPairedWithSalt
import org.litote.kmongo.Id

interface AccountRepository{
    suspend fun createNew(account: AccountServer): Boolean
    suspend fun checkIfUsernameExists(username: String): Boolean
    suspend fun deleteById(id: Id<AccountServer>): Boolean
    suspend fun getHashSaltPairById(id: Id<AccountServer>): HashPairedWithSalt?
    suspend fun getHashSaltPairByUsername(username: String): HashPairedWithSalt?
}
