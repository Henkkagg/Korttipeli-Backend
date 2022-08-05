package com.example.domain.usecase.cards

import com.example.domain.repository.CardsRepository

class VerifyAuthority(private val repository: CardsRepository) {

    suspend operator fun invoke(id: String, username: String): Boolean {

        return username == repository.getAuthorById(id)
    }
}