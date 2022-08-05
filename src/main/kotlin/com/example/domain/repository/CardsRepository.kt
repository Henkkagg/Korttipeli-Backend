package com.example.domain.repository

import com.example.data.model.*
import com.example.domain.model.NewCardInfo
import com.example.domain.usecase.CardResult

interface CardsRepository {

    suspend fun getIdsByAuthors(authorList: List<String>): List<CardIdsServer>

    suspend fun getUpdatesByIds(
        imageIdsList: List<String>,
        infoIdsList: List<String>
    ): Pair<List<UpdatedImage>, List<UpdatedInfo>>

    suspend fun getCardsByIds(idList: List<String>): List<Card>

    suspend fun getAuthorById(id: String): String

    suspend fun getAllDecksForUser(userId: String)

    suspend fun getTempImage(id: String): String?

    suspend fun createTempImage(base64Image: String): CardResult

    suspend fun createCard(card: Card): Boolean

    suspend fun updateInfoAndImage(card: CardUpdatedInfoAndImage): Boolean

    suspend fun updateInfo(cardUpdatedInfo: CardUpdatedInfo): Boolean

    suspend fun deleteCard(cardId: String): Boolean

}