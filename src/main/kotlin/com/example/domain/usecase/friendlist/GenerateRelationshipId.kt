package com.example.domain.usecase.friendlist

class GenerateRelationshipId {

    operator fun invoke(currentUser: String, targetUser: String): Long {

        return currentUser.hashCode().toLong() + targetUser.hashCode().toLong()
    }
}