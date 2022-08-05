package com.example.data.repository

import com.example.data.model.*
import com.example.domain.model.NewCardInfo
import com.example.domain.repository.CardsRepository
import com.example.domain.usecase.CardResult
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase

class CardsRepositoryImpl(db: CoroutineDatabase) : CardsRepository {
    private val tempImageCol = db.getCollection<TempImage>("temp_images")
    private val cardsCol = db.getCollection<Card>("cards")
    override suspend fun getIdsByAuthors(authorList: List<String>): List<CardIdsServer> {
        val projection = include(Card::_id, Card::idForImage, Card::idForOtherThanImage)
        //data class CardIdsServer(val _id: String, val idForImage: String, val idForOtherThanImage: String)

        return cardsCol.withDocumentClass<CardIdsServer>().find(Card::author `in` authorList)
            .projection(projection).toList()
    }

    override suspend fun getUpdatesByIds(
        imageIdsList: List<String>,
        infoIdsList: List<String>
    ): Pair<List<UpdatedImage>, List<UpdatedInfo>> {
        val imageProjection = include(Card::_id, Card::idForImage, Card::base64Image)
        val infoProjection = include(
            Card::_id,
            Card::idForOtherThanImage,
            Card::title,
            Card::description,
            Card::type
        )
        val updatedImagesList = cardsCol.withDocumentClass<UpdatedImage>().find(Card::_id `in` imageIdsList)
            .projection(imageProjection).toList()
        val updatedInfoList = cardsCol.withDocumentClass<UpdatedInfo>().find(Card::_id `in` infoIdsList)
            .projection(infoProjection).toList()

        return Pair(updatedImagesList, updatedInfoList)
    }

    override suspend fun getCardsByIds(idList: List<String>): List<Card> {

        return cardsCol.find(Card::_id `in` idList).toList()
    }

    override suspend fun getAuthorById(id: String): String {
        val projection = include(Card::author)
        data class AuthorOnly(val author: String)

        return cardsCol.withDocumentClass<AuthorOnly>().findOne(Card::_id eq id)?.author ?: ""
    }

    override suspend fun getAllDecksForUser(userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun createTempImage(base64Image: String): CardResult {
        val tempImage = TempImage(image = base64Image)
        val success = tempImageCol.insertOne(tempImage).wasAcknowledged()

        return if (success) CardResult.Success(tempImage._id) else CardResult.ServerError
    }

    override suspend fun getTempImage(id: String): String? {

        return tempImageCol.findOne(TempImage::_id eq id)?.image
    }

    override suspend fun createCard(card: Card): Boolean {

        return cardsCol.insertOne(card).wasAcknowledged()
    }

    override suspend fun updateInfoAndImage(card: CardUpdatedInfoAndImage): Boolean {

        return cardsCol.updateOneById(card._id, card).wasAcknowledged()
    }

    override suspend fun updateInfo(card: CardUpdatedInfo): Boolean {

        return cardsCol.updateOneById(card._id, card).wasAcknowledged()
    }

    override suspend fun deleteCard(cardId: String): Boolean {

        return cardsCol.deleteOneById(cardId).wasAcknowledged()
    }
}