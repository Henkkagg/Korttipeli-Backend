package com.example.domain.usecase.cards

import com.example.data.model.CardIdsServer
import com.example.domain.repository.CardsRepository

class GetIdsByAuthors(private val repository: CardsRepository) {

    suspend operator fun invoke(authorList: List<String>): List<CardIdsServer> {

        return repository.getIdsByAuthors(authorList)
    }
}