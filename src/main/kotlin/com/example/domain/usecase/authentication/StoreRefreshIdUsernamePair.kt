package com.example.domain.usecase.authentication

import com.example.data.model.RefreshIdUsernamePair
import com.example.domain.repository.AuthenticationRepository

class StoreRefreshIdUsernamePair(private val repository: AuthenticationRepository) {

    suspend operator fun invoke(refreshIdUsernamePair: RefreshIdUsernamePair): Boolean {

        return repository.create(refreshIdUsernamePair)
    }
}