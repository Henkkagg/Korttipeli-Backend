package com.example.domain.repository

import com.example.data.dto.RelationshipStatus

interface FriendlistRepository {
    suspend fun getFriendsByUsername(username: String): List<String>

    suspend fun getPendingForCurrentUsername(username: String): List<String>

    suspend fun getPendingForTargetUsernarme(username: String): List<String>

    suspend fun getRelationshipStatusesByUsername(username: String): List<RelationshipStatus>

    suspend fun getRelationshipStatusById(relationshipId: Long): RelationshipStatus?

    suspend fun acceptFriendRequest(relationshipId: Long): Boolean

    suspend fun removeFriend(relationshipId: Long): Boolean

    suspend fun createNewRelationship(relationshipStatus: RelationshipStatus): Boolean
}