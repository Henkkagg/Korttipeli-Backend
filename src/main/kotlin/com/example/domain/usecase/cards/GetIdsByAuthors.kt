package com.example.domain.usecase.cards

import com.example.data.dto.IdsServer
import com.example.domain.repository.CardsRepository

class GetIdsByAuthors(private val repository: CardsRepository) {

    suspend operator fun invoke(authorList: List<String>): List<IdsServer> {

        return repository.getIdsByAuthors(authorList)
    }
}