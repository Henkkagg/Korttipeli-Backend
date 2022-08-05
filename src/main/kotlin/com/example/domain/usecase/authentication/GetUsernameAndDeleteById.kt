package com.example.domain.usecase.authentication

import com.example.data.model.RefreshIdUsernamePair
import com.example.domain.repository.AuthenticationRepository
import org.litote.kmongo.Id

class GetUsernameAndDeleteById(private val repository: AuthenticationRepository) {

    suspend operator fun invoke(id: String): String? {

        return repository.findUsernameAndDeleteById(id)
    }
}