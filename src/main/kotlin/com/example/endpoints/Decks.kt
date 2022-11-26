package com.example.endpoints

import com.example.data.dto.IdsServer
import com.example.domain.logicflow.Decks
import com.example.domain.model.DeckFromClient
import com.example.domain.usecase.DeckResult
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.decks() {

    val decks = Decks()

    get("/decks/get") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@get
        val idsList = call.receive<List<IdsServer>>()
        println(idsList)

        val decksDataPackage = decks.getDecks(idsList, username)
        println(decksDataPackage.newDecks.map { it._id })
        println(decksDataPackage.updatedImages.map { it._id })
        println(decksDataPackage.updatedNonImages.map { it._id })
        println(decksDataPackage.decksToDelete.map { it })
        call.respond(decksDataPackage)
    }

    post("/decks/create") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@post

        val body = call.receive<String>()
        val deckFromClient = Gson().fromJson(body, DeckFromClient::class.java)

        val result = decks.createDeck(deckFromClient, username)
        when (result) {
            is DeckResult.Success -> call.respond(HttpStatusCode.OK, result.idsServer)
            DeckResult.NameIssue -> call.respond(HttpStatusCode.NotAcceptable)
            DeckResult.CardAmountIssue -> call.respond(HttpStatusCode.LengthRequired)
            DeckResult.ImageIssue -> call.respond(HttpStatusCode.PayloadTooLarge)
            else -> call.respond(HttpStatusCode.ExpectationFailed)
        }
    }

    post("/decks/update") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@post

        val body = call.receive<String>()
        val deckFromClient = Gson().fromJson(body, DeckFromClient::class.java)

        val result = decks.updateDeck(deckFromClient, username)
        when (result) {
            is DeckResult.Success -> call.respond(HttpStatusCode.OK, result.idsServer)
            DeckResult.NameIssue -> call.respond(HttpStatusCode.NotAcceptable)
            DeckResult.CardAmountIssue -> call.respond(HttpStatusCode.LengthRequired)
            DeckResult.ImageIssue -> call.respond(HttpStatusCode.PayloadTooLarge)
            else -> call.respond(HttpStatusCode.ExpectationFailed)
        }
    }

    post("/decks/delete") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@post

        val deckId = call.receiveText()

        val success = decks.deleteDeck(deckId, username)
        if (success) call.respond(HttpStatusCode.OK, deckId) else call.respond(HttpStatusCode.ExpectationFailed)
    }

}