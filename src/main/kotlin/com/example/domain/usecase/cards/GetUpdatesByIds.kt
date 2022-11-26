package com.example.domain.usecase.cards

import com.example.data.dto.UpdatedImage
import com.example.data.dto.UpdatedInfo
import com.example.domain.repository.CardsRepository

class GetUpdatesByIds(private val repository: CardsRepository) {

    suspend operator fun invoke(
        imageIdsList: List<String>,
        infoIdsList: List<String>
    ): Pair<List<UpdatedImage>, List<UpdatedInfo>> {

        return repository.getUpdatesByIds(imageIdsList, infoIdsList)
    }
}