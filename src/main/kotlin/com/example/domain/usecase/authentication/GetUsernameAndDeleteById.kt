package com.example.domain.usecase.authentication

import com.example.domain.repository.AuthenticationRepository

class GetUsernameAndDeleteById(private val repository: AuthenticationRepository) {

    suspend operator fun invoke(id: String): String? {

        return repository.findUsernameAndDeleteById(id)
    }
}