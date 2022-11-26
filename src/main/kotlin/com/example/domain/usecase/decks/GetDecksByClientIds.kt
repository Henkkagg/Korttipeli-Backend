package com.example.domain.usecase.decks

import com.example.data.dto.IdsServer
import com.example.domain.model.DecksDataPackage
import com.example.domain.repository.CardsRepository
import com.example.domain.repository.DecksRepository
import com.example.domain.repository.FriendlistRepository

/*
What this does:
1. Get the user's friends and add username to the list
2. Get db references for all decks created by friends and by user
3. Get specific ids for decks that the user is authorized to use
3.1 If deck contains cards that are no longer in db, deck's cardIds need to be updated
4. Check if user is missing / has redundant / outdated decks, and include necessary changes in the response
 */
class GetDecksByClientIds(
    private val decksRepository: DecksRepository,
    private val cardsRepository: CardsRepository,
    private val friendlistRepository: FriendlistRepository,
) {
    suspend operator fun invoke(idsFromClient: List<IdsServer>, username: String): DecksDataPackage {

        //1. Get the user's friends and add username to the list
        val friendsAndUser = friendlistRepository.getFriendsByUsername(username) + username

        //2. Get db references for all decks created by friends and by user
        val uncheckedDecks = decksRepository.getDecksByAuthors(friendsAndUser)

        //3. Get specific ids for decks that the user is authorized to use
        val authorizedDecksToFetch = mutableListOf<String>()

        uncheckedDecks.forEach {deckId ->
            val uncheckedCardIds = decksRepository.getCards(deckId)
            val foundIds = cardsRepository.checkExistances(uncheckedCardIds)

            //3.1 If deck contains cards that are no longer in db, deck's cardIds need to be updated
            if (foundIds.size != uncheckedCardIds.size) {
                decksRepository.updateCards(deckId, foundIds)
            }

            val authorsRequired = cardsRepository.getAuthorsByIds(foundIds).distinct()
            if (friendsAndUser.containsAll(authorsRequired)) authorizedDecksToFetch.add(deckId)
        }

        val authorizedDeckIds = decksRepository.getIdsByIds(authorizedDecksToFetch)

        //4. Check if user is missing / has redundant / outdated decks, and include necessary changes in the response

        //These are to enable us to batch db queries
        val needNewDeck = mutableListOf<String>()
        val needEverythingUpdated = mutableListOf<String>()
        val needImageUpdated = mutableListOf<String>()
        val needOtherThanImageUpdated = mutableListOf<String>()

        val clientIdsMap = hashMapOf<String, IdsServer>()
        idsFromClient.forEach {
            clientIdsMap[it._id] = it
        }


        authorizedDeckIds.forEach {
            if (clientIdsMap.contains(it._id)) {

                //Client has the deck and it is...
                when {
                    //...up-to-date
                    clientIdsMap[it._id]?.idForImage == it.idForImage
                            && clientIdsMap[it._id]?.idForNonImage == it.idForNonImage -> {}

                    //...image and other information is outdated
                    clientIdsMap[it._id]?.idForImage != it.idForImage
                            && clientIdsMap[it._id]?.idForImage != it.idForNonImage -> {
                                needEverythingUpdated.add(it._id)
                            }

                    //...just the image is outdated
                    clientIdsMap[it._id]?.idForImage != it.idForImage -> needImageUpdated.add(it._id)

                    //...just the other information is outdated
                    clientIdsMap[it._id]?.idForNonImage != it.idForNonImage -> {
                        needOtherThanImageUpdated.add(it._id)
                    }
                }
                clientIdsMap.remove(it._id)

            } else needNewDeck.add(it._id)
        }

        //Anything that remained in clientIds must be a removed or an unauthorized deck
        val decksToDelete = mutableListOf<String>()
        clientIdsMap.forEach { decksToDelete.add(it.key) }

        val newDecks = decksRepository.getNewDecks(needNewDeck)
        val updatedImages = decksRepository.getImages(needImageUpdated + needEverythingUpdated)
        val updatedNonImages = decksRepository.getOtherThanImages(needOtherThanImageUpdated + needEverythingUpdated)

        return DecksDataPackage(newDecks, updatedImages, updatedNonImages, decksToDelete)
    }
}