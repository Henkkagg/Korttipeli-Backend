package com.example.domain.usecase

import com.example.domain.usecase.friendlist.*

data class FriendlistUsecases(
    val generateRelationshipId: GenerateRelationshipId,
    val getRelationshipStatus: GetRelationshipStatus,
    val createNewRelationship: CreateNewRelationship,
    val acceptFriendRequest: AcceptFriendRequest,
    val getFriendlist: GetFriendlist,
    val getRelationshipStatuses: GetRelationshipStatuses,
    val parseRelationshipStatuses: ParseRelationshipStatuses,
    val removeFriend: RemoveFriend
)

sealed class RelationshipResult {
    object FriendshipMade: RelationshipResult()
    object AlreadyFriends: RelationshipResult()
    object PendingApproval: RelationshipResult()
    object AlreadyPendingApproval: RelationshipResult()
    object UserNotFound: RelationshipResult()
    object CantBefriendSelf: RelationshipResult()
    object UnknownError: RelationshipResult()
}