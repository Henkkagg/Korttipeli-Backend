package com.example.domain.usecase.account_management

import com.example.domain.model.HashPairedWithSalt
import com.example.domain.repository.AccountRepository

class GetHashSaltPair(private val repository: AccountRepository) {

    suspend operator fun invoke(username: String): HashPairedWithSalt? {

        return repository.getHashSaltPairByUsername(username)
    }
}