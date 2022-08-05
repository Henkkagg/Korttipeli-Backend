package com.example.domain.repository

import com.example.data.model.RefreshIdUsernamePair
import org.litote.kmongo.Id

interface AuthenticationRepository {
    suspend fun findUsernameAndDeleteById(id: String): String?
    suspend fun deleteByUsername(username: String): Boolean
    suspend fun create(refreshIdUsernamePair: RefreshIdUsernamePair): Boolean
}