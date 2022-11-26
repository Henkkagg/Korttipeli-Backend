package com.example.data.repository

import com.example.data.dto.*
import com.example.domain.repository.CardsRepository
import com.example.domain.usecase.CardResult
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.projection
import org.litote.kreflect.findProperty

class CardsRepositoryImpl(db: CoroutineDatabase) : CardsRepository {
    private val tempImageCol = db.getCollection<TempImage>("temp_images")
    private val cardsCol = db.getCollection<Card>("cards")
    override suspend fun getIdsByAuthors(authorList: List<String>): List<IdsServer> {
        val projection = include(Card::_id, Card::idForImage, Card::idForNonImage)

        return cardsCol.withDocumentClass<IdsServer>().find(Card::author `in` authorList)
            .projection(projection).toList()
    }

    override suspend fun getUpdatesByIds(
        imageIdsList: List<String>,
        infoIdsList: List<String>
    ): Pair<List<UpdatedImage>, List<UpdatedInfo>> {
        val imageProjection = include(Card::_id, Card::idForImage, Card::base64Image)
        val infoProjection = include(
            Card::_id,
            Card::idForNonImage,
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

    override suspend fun checkExistances(cardIds: List<String>): List<String> {

        return cardsCol.projection(Card::_id).filter(Card::_id `in` cardIds).toList()
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

    override suspend fun updateInfo(cardUpdatedInfo: CardUpdatedInfo): Boolean {

        return cardsCol.updateOneById(cardUpdatedInfo._id, cardUpdatedInfo).wasAcknowledged()
    }

    override suspend fun deleteCard(cardId: String): Boolean {

        return cardsCol.deleteOneById(cardId).wasAcknowledged()
    }

    override suspend fun addDeckIdToCards(deckId: String, cardIds: List<String>): Boolean {

        return cardsCol.updateMany(Card::_id `in` cardIds, push(Card::containedInDecks, deckId)).wasAcknowledged()
    }

    override suspend fun getAuthorsByIds(cardIds: List<String>): List<String> {

        return cardsCol.projection(Card::author).filter(Card::_id `in` cardIds).toList()
    }

    override suspend fun getIdAuthorPairsByIds(cardIds: List<String>): List<Pair<String?, String?>> {

        return cardsCol.projection(Card::_id, Card::author).filter(Card::_id `in` cardIds).toList()
    }
}