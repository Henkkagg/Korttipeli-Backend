package com.example.domain.usecase.account_management

import com.example.data.dto.AccountServer
import com.example.domain.repository.AccountRepository

class CreateNewAccount(private val repository: AccountRepository) {

    suspend operator fun invoke(accountserver: AccountServer): Boolean {
        return repository.createNew(accountserver)
    }
}