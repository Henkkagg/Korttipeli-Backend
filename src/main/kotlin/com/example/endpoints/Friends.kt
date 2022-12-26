package com.example.endpoints

import com.example.domain.cleanName
import com.example.domain.logicflow.Friends
import com.example.domain.usecase.RelationshipResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.friends() {
    val friends = Friends()
    route("/friendlist") {

    }
    get("/friendlist") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@get

        val friendlist = friends.getAllFriends(username)
        call.respond(friendlist)
    }

    post("/friendlist/add") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@post
        val targetUser = call.receive<String>().cleanName()
        if (targetUser.isEmpty()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val result = friends.addNew(username, targetUser)
        when (result) {
            RelationshipResult.FriendshipMade -> {
                call.respond(HttpStatusCode.OK)
            }
            RelationshipResult.AlreadyFriends -> {
                call.respond(HttpStatusCode.Found)
            }
            RelationshipResult.PendingApproval -> {
                call.respond(HttpStatusCode.OK)
            }
            RelationshipResult.AlreadyPendingApproval -> {
                call.respond(HttpStatusCode.Found)
            }
            RelationshipResult.CantBefriendSelf -> {
                call.respond(HttpStatusCode.BadRequest)
            }
            RelationshipResult.UserNotFound -> {
                call.respond(HttpStatusCode.NotFound)
            }
            RelationshipResult.UnknownError -> {
                call.respond(HttpStatusCode.ExpectationFailed)
            }
        }
    }

    post("/friendlist/remove") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@post
        val targetUser = call.receive<String>()
        if (targetUser.isEmpty()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val result = friends.removeRelationship(username, targetUser)
        if (result) call.respond(HttpStatusCode.OK) else call.respond(HttpStatusCode.ExpectationFailed)
    }
}