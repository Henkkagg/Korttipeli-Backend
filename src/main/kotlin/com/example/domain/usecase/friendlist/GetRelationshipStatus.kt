package com.example.domain.usecase.friendlist

import com.example.data.dto.RelationshipStatus
import com.example.domain.repository.FriendlistRepository

class GetRelationshipStatus(private val repository: FriendlistRepository) {

    suspend operator fun invoke(relationshipId: Long): RelationshipStatus? {

        return repository.getRelationshipStatusById(relationshipId)
    }
}