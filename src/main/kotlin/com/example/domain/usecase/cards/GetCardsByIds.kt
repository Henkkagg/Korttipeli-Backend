package com.example.domain.usecase.cards

import com.example.data.model.Card
import com.example.domain.repository.CardsRepository

class GetCardsByIds(private val repository: CardsRepository) {

    suspend operator fun invoke(idList: List<String>): List<Card> {

        return repository.getCardsByIds(idList)
    }
}