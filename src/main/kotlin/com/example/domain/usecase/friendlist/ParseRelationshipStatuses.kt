package com.example.domain.usecase.friendlist

import com.example.data.dto.RelationshipStatus
import com.example.domain.model.RelationshipStatuses

class ParseRelationshipStatuses {

    suspend operator fun invoke(relationshipList: List<RelationshipStatus>, username: String): RelationshipStatuses {

        val friendsWith = mutableListOf<String>()
        val sentRequests = mutableListOf<String>()
        val receivedRequests = mutableListOf<String>()

        relationshipList.forEach {

            //1 = username1 needs to approve, 2 = username2 needs to approve, 3 = friends
            if (it.username1 == username) {
                when (it.status) {
                    1 -> {
                        receivedRequests.add(it.username2)
                    }
                    2 -> {
                        sentRequests.add(it.username2)
                    }
                    3 -> {
                        friendsWith.add(it.username2)
                    }
                }
            }
            if (it.username2 == username) {
                when (it.status) {
                    1 -> {
                        sentRequests.add(it.username1)
                    }
                    2 -> {
                        receivedRequests.add(it.username1)
                    }
                    3 -> {
                        friendsWith.add(it.username1)
                    }
                }
            }
        }

        return RelationshipStatuses(
            friendsWith.sortedDescending().reversed(),
            sentRequests.sortedDescending().reversed(),
            receivedRequests.sortedDescending().reversed()
        )
    }
}