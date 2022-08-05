package com.example.domain.usecase.friendlist

import com.example.domain.repository.FriendlistRepository

class RemoveFriend(private val repository: FriendlistRepository) {

    suspend operator fun invoke(relationshipId: Long): Boolean {

        return repository.removeFriend(relationshipId)
    }
}