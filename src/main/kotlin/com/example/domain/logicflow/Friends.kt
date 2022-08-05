package com.example.domain.logicflow

import com.example.data.model.RelationshipStatus
import com.example.domain.model.RelationshipStatuses
import com.example.domain.usecase.AccountManagementUsecases
import com.example.domain.usecase.FriendlistUsecases
import com.example.domain.usecase.RelationshipResult
import org.koin.java.KoinJavaComponent.inject

class Friends{
    private val friendList: FriendlistUsecases by inject(FriendlistUsecases::class.java)
    private val accountManagement: AccountManagementUsecases by inject(AccountManagementUsecases::class.java)

    suspend fun addNew(currentUser: String, targetUser: String): RelationshipResult {
        val relationshipId = friendList.generateRelationshipId(currentUser, targetUser)
        val relationshipStatus = friendList.getRelationshipStatus(relationshipId)

        if (currentUser == targetUser) return RelationshipResult.CantBefriendSelf
        if (!accountManagement.verifyUsernameLegality(targetUser)) return RelationshipResult.UserNotFound

        val targetExists = accountManagement.checkIfUsernameExists(targetUser)
        if (!targetExists) return RelationshipResult.UserNotFound

        if (relationshipStatus == null) {
            val newRelationship = RelationshipStatus(relationshipId, currentUser, targetUser, 2)
            val success = friendList.createNewRelationship(newRelationship)
            return if (success) RelationshipResult.PendingApproval else RelationshipResult.UnknownError
        }

        when (relationshipStatus.status) {

            1 -> {
                if (currentUser != relationshipStatus.username1) return RelationshipResult.AlreadyPendingApproval
                friendList.acceptFriendRequest(relationshipId)
                return RelationshipResult.FriendshipMade
            }
            2 -> {
                if (currentUser != relationshipStatus.username2) return RelationshipResult.AlreadyPendingApproval
                friendList.acceptFriendRequest(relationshipId)
                return RelationshipResult.FriendshipMade
            }
            else -> {
                return RelationshipResult.AlreadyFriends
            }
        }
    }

    suspend fun getAllFriends(username: String): RelationshipStatuses {

        val relationshipList = friendList.getRelationshipStatuses(username)
        return friendList.parseRelationshipStatuses(relationshipList, username)
    }

    suspend fun removeRelationship(currentUser: String, targetUser: String) : Boolean {
        val relationshipId = friendList.generateRelationshipId(currentUser, targetUser)
        return friendList.removeFriend(relationshipId)
    }
}