package com.example.data.repository

import com.example.data.model.RelationshipStatus
import com.example.domain.repository.FriendlistRepository
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.include
import org.litote.kmongo.or
import org.litote.kmongo.setValue

class FriendlistRepositoryImpl(db: CoroutineDatabase) : FriendlistRepository {
    private val col = db.getCollection<RelationshipStatus>("relationships")

    data class Username1(val username2: String)
    data class Username2(val username1: String)

    private val projection1 = include(RelationshipStatus::username2)
    private val projection2 = include(RelationshipStatus::username1)

    override suspend fun getFriendsByUsername(username: String): List<String> {
        val usernameList = mutableListOf<String>()

        col.withDocumentClass<Username1>().find("{username1: '$username', status: 3}")
            .projection(projection1).toList().forEach { usernameList.add(it.username2) }
        col.withDocumentClass<Username2>().find("{username2: '$username', status: 3}")
            .projection(projection2).toList().forEach { usernameList.add(it.username1) }

        return usernameList
    }

    override suspend fun getPendingForCurrentUsername(username: String): List<String> {
        val usernameList = mutableListOf<String>()

        col.withDocumentClass<Username1>().find("{username1: '$username', status: 1}")
            .projection(projection1).toList().forEach { usernameList.add(it.username2) }
        col.withDocumentClass<Username2>().find("{username2: '$username', status: 2}")
            .projection(projection2).toList().forEach { usernameList.add(it.username1) }

        return usernameList
    }

    override suspend fun getPendingForTargetUsernarme(username: String): List<String> {
        val usernameList = mutableListOf<String>()

        col.withDocumentClass<Username1>().find("{username1: '$username', status: 2}")
            .projection(projection1).toList().forEach { usernameList.add(it.username2) }
        col.withDocumentClass<Username2>().find("{username2: '$username', status: 1}")
            .projection(projection2).toList().forEach { usernameList.add(it.username1) }

        return usernameList
    }

    override suspend fun getRelationshipStatusesByUsername(username: String): List<RelationshipStatus> {

        val list = col
            .find(or(RelationshipStatus::username1 eq username, RelationshipStatus::username2 eq username))
            .toList()

        return list
    }

    override suspend fun getRelationshipStatusById(relationshipId: Long): RelationshipStatus? {

        return col.find(RelationshipStatus::_id eq relationshipId).first()
    }

    override suspend fun acceptFriendRequest(relationshipId: Long): Boolean {

        return col.updateOne(
            RelationshipStatus::_id eq relationshipId, setValue(RelationshipStatus::status, 3)
        )
            .wasAcknowledged()
    }

    override suspend fun removeFriend(relationshipId: Long): Boolean {

        return col.deleteOne(RelationshipStatus::_id eq relationshipId).wasAcknowledged()
    }

    override suspend fun createNewRelationship(relationshipStatus: RelationshipStatus): Boolean {

        return col.insertOne(relationshipStatus).wasAcknowledged()
    }
}