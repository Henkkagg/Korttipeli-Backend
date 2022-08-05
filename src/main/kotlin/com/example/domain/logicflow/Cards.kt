package com.example.domain.logicflow

import com.example.data.model.CardIdsServer
import com.example.domain.model.CardDataPackage
import com.example.domain.model.NewCardInfo
import com.example.domain.usecase.CardResult
import com.example.domain.usecase.CardsUsecases
import com.example.domain.usecase.FriendlistUsecases
import org.koin.java.KoinJavaComponent.inject

class Cards {
    private val cards: CardsUsecases by inject(CardsUsecases::class.java)
    private val friendlist: FriendlistUsecases by inject(FriendlistUsecases::class.java)

    suspend fun createCard(newCardInfo: NewCardInfo, username: String): CardResult {

        val contentLegalityResult = cards.verifyContentLegality(newCardInfo)
        if (contentLegalityResult !is CardResult.Success) return contentLegalityResult

        return cards.createCard(newCardInfo, username)
    }

    suspend fun createTempImage(base64Image: String): CardResult {

        return cards.createTempImage(base64Image)
    }

    suspend fun getAllCards(cardIdsClientList: List<CardIdsServer>, username: String): CardDataPackage {

        //Check whose cards we should give the client
        val authorList = friendlist.getFriendlist(username) + username
        val cardIdsServerList = cards.getIdsByAuthors(authorList)

        val cardsClientShouldGet = cardIdsServerList.filterNot { serverObject ->
            cardIdsClientList.map { it._id }.contains(serverObject._id)
        }.map { it._id }
        val cardsClientShouldDelete = cardIdsClientList.filterNot { clientObject ->
            cardIdsServerList.map { it._id }.contains(clientObject._id)
        }.map { it._id }

        //Represents cards that the client has stored in local db. Need to check if image or other details have changed
        val idsThatNeedChecking = cardIdsClientList.filter { clientObject ->
            cardIdsServerList.map { it._id }.contains(clientObject._id)
        }.sortedBy { it._id }
        val idsToCheckAgainst = cardIdsServerList.filter { serverObject ->
            idsThatNeedChecking.map { it._id }.contains(serverObject._id)
        }.sortedBy { it._id }

        val nonMatchingImages = mutableListOf<String>()
        val nonMatchingInfos = mutableListOf<String>()
        for (i in idsThatNeedChecking.indices) {
            if (idsThatNeedChecking[i].idForImage != idsToCheckAgainst[i].idForImage) {
                nonMatchingImages.add(idsToCheckAgainst[i]._id)
            }
            if (idsThatNeedChecking[i].idForOtherThanImage != idsToCheckAgainst[i].idForOtherThanImage) {
                nonMatchingInfos.add(idsToCheckAgainst[i]._id)
            }
        }
        val (updatedImageList, updatedInfoList) = cards.getUpdatesByIds(nonMatchingImages, nonMatchingInfos)
        val newCardsList = cards.getCardsByIds(cardsClientShouldGet)

        return CardDataPackage(
            cardsToDelete = cardsClientShouldDelete,
            newCards = newCardsList,
            updatedInfos = updatedInfoList,
            updatedImages = updatedImageList
        )
    }

    suspend fun updateCard(cardInfo: NewCardInfo, username: String): CardResult {

        if (!cards.verifyAuthority(cardInfo.id, username)) return CardResult.ServerError

        println(cardInfo)
        val contentLegalityResult = cards.verifyContentLegality(cardInfo)
        if (contentLegalityResult !is CardResult.Success) return contentLegalityResult

        return cards.updateCard(cardInfo)
    }

    suspend fun deleteCard(cardId: String, username: String): CardResult {
        if (!cards.verifyAuthority(cardId, username)) return CardResult.ServerError

        return if (cards.deleteCard(cardId)) CardResult.Success() else CardResult.ServerError
    }
}