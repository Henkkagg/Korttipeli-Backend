package com.example.data.repository

import com.example.data.dto.*
import com.example.domain.model.NewDeck
import com.example.domain.repository.DecksRepository
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.projection

class DecksReposotiryImpl(db: CoroutineDatabase) : DecksRepository {
    private val col = db.getCollection<Deck>("decks")

    override suspend fun createDeck(deck: Deck): Boolean {

        return col.insertOne(deck).wasAcknowledged()
    }

    override suspend fun getNewDecks(ids: List<String>): List<NewDeck> {
        val projection = include(
            Deck::_id,
            Deck::idForImage,
            Deck::idForNonImage,
            Deck::name,
            Deck::base64Image,
            Deck::author,
            Deck::cardIds
        )

        return col.withDocumentClass<NewDeck>().find(Deck::_id `in` ids).projection(projection).toList()
    }

    override suspend fun getDecksByAuthors(authors: List<String>): List<String> {

        return col.projection(Deck::_id).filter(Deck::author `in` authors).toList()
    }

    override suspend fun getAuthorById(deckId: String): String {

        return col.projection(Deck::author).filter(Deck::_id eq deckId).first() ?: ""
    }

    override suspend fun getImages(ids: List<String>): List<UpdatedImage> {
        val projection = include(Deck::_id, Deck::idForImage, Deck::base64Image)

        return col.withDocumentClass<UpdatedImage>().find(Deck::_id `in` ids).projection(projection).toList()
    }

    override suspend fun getOtherThanImages(ids: List<String>): List<DeckUpdatedNonImage> {
        val projection = include(Deck::_id, Deck::idForNonImage, Deck::name, Deck::author, Deck::cardIds)

        return col.withDocumentClass<DeckUpdatedNonImage>().find(Deck::_id `in` ids)
            .projection(projection).toList()
    }

    override suspend fun updateDeck(deck: Deck): Boolean {

        return col.updateOneById(deck._id, deck).wasAcknowledged()
    }

    override suspend fun updateCards(deckId: String, cardIds: List<String>): Boolean {
        val idForNonImage = newId<Deck>().toString()

        return col.updateOneById(deckId, set(Deck::cardIds setTo cardIds, Deck::idForNonImage setTo idForNonImage))
            .wasAcknowledged()
    }

    override suspend fun getCards(deckId: String): List<String> {

        return col.projection(Deck::cardIds).filter(Deck::_id eq deckId).first() ?: emptyList()
    }

    override suspend fun getIdsByIds(deckIds: List<String>): List<IdsServer> {

        return col.withDocumentClass<IdsServer>().find(Deck::_id `in` deckIds)
            .projection(Deck::_id, Deck::idForImage, Deck::idForNonImage).toList()
    }

    override suspend fun deleteDeck(deckId: String): Boolean {

        return col.deleteOneById(deckId).wasAcknowledged()
    }

}