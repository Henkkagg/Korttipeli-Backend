package com.example.domain.usecase.friendlist

import com.example.data.model.RelationshipStatus
import com.example.domain.repository.FriendlistRepository

class GetRelationshipStatuses(private val repository: FriendlistRepository) {

    suspend operator fun invoke(username: String): List<RelationshipStatus> {

        return repository.getRelationshipStatusesByUsername(username)
    }
}