package com.example.domain.usecase.cards

import com.example.domain.repository.CardsRepository
import com.example.domain.usecase.CardResult

class CreateTempImage(private val repository: CardsRepository) {

    suspend operator fun invoke(base6Image: String): CardResult {

        val attemptedSizeInKb = base6Image.length / 1000
        val allowedSizeInKb = 500
        if (attemptedSizeInKb > allowedSizeInKb) return CardResult.ImageTooLarge(attemptedSizeInKb, allowedSizeInKb)

        return repository.createTempImage(base6Image)
    }


}