package com.example.domain.usecase.friendlist

import com.example.data.model.RelationshipStatus
import com.example.domain.repository.FriendlistRepository

class CreateNewRelationship(private val repository: FriendlistRepository) {

    suspend operator fun invoke(relationshipStatus: RelationshipStatus): Boolean {

        return repository.createNewRelationship(relationshipStatus)
    }
}