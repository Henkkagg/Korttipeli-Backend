package com.example.domain.usecase.authentication

import com.example.domain.repository.AuthenticationRepository

class DeleteByUsername(private val repository: AuthenticationRepository) {

    suspend operator fun invoke(username: String): Boolean {
        return repository.deleteByUsername(username)
    }
}