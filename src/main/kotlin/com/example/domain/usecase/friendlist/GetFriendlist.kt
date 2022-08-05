package com.example.domain.usecase.friendlist

import com.example.domain.repository.FriendlistRepository

class GetFriendlist(private val repository: FriendlistRepository) {

    suspend operator fun invoke(username: String): List<String> {

        return repository.getFriendsByUsername(username).sortedDescending().reversed()
    }
}