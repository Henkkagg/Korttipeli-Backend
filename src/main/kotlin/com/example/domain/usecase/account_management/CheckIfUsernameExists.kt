package com.example.domain.usecase.account_management

import com.example.domain.repository.AccountRepository

class CheckIfUsernameExists(private val repository: AccountRepository) {

    suspend operator fun invoke(username: String): Boolean {

        return repository.checkIfUsernameExists(username)
    }
}