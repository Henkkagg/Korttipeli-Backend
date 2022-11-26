package com.example.endpoints

import com.example.data.dto.IdsServer
import com.example.domain.logicflow.Cards
import com.example.domain.model.NewCardInfo
import com.example.domain.usecase.CardResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.cards() {
    val cards = Cards()

    get("/cards/get") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@get

        val idsList = call.receive<List<IdsServer>>()

        runCatching {
            val cardDataPackage = cards.getAllCards(idsList, username)
            call.respond(cardDataPackage)
        }.onFailure {
            call.respond(HttpStatusCode.ExpectationFailed)
        }
    }

    post("/cards/uploadimage") {
        val base64Image = call.receive<String>()

        val result = cards.createTempImage(base64Image)
        when (result) {
            is CardResult.Success -> {
                call.respond(HttpStatusCode.OK, result.id.toString())
            }
            is CardResult.ImageTooLarge -> {
                call.respond(
                    HttpStatusCode.NotAcceptable, hashMapOf(
                        "attemptedSizeInKb" to result.attemptedSizeInKb, "allowedSizeInKb" to result.allowedSizeInKb
                    )
                )
            }
            else -> {
                call.respond(HttpStatusCode.ExpectationFailed)
            }
        }
    }

    post("/cards/create") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@post

        val newCardInfo = call.receive<NewCardInfo>()
        val result = cards.createCard(newCardInfo, username)

        when (result) {
            is CardResult.Success -> {
                call.respond(
                    HttpStatusCode.OK, mapOf(
                        "id" to result.id,
                        "idForImage" to result.idForImage,
                        "idForOtherThanImage" to result.idForOtherThanImage,
                        "author" to result.author
                    )
                )
            }
            is CardResult.DescriptionTooLong -> {
                call.respond(
                    HttpStatusCode.PayloadTooLarge, mapOf(
                        "attemptedLength" to result.attemptedLength,
                        "allowedLength" to result.allowedLength
                    )
                )
            }
            is CardResult.TitleTooLong -> {
                call.respond(
                    HttpStatusCode.UnprocessableEntity, mapOf(
                        "attemptedLength" to result.attemptedLength,
                        "allowedLength" to result.allowedLength
                    )
                )
            }
            is CardResult.TitleAndDescriptionTooLong -> {
                call.respond(
                    HttpStatusCode.MultiStatus, mapOf(
                        "attemptedTitleLength" to result.attemptedTitleLength,
                        "allowedTitleLenhtj" to result.allowedTitleLength,
                        "attemptedDescriptionLength" to result.attemptedDescriptionLength,
                        "allowedDescriptionLength" to result.allowedDescriptionLength
                    )
                )
            }
            else -> call.respond(HttpStatusCode.ExpectationFailed)
        }
    }

    post("/cards/update") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@post

        val cardInfo = call.receive<NewCardInfo>()

        val result = cards.updateCard(cardInfo, username)
        when (result) {
            is CardResult.Success -> {
                call.respond(
                    HttpStatusCode.OK, mapOf(
                        "id" to result.id,
                        "idForImage" to result.idForImage,
                        "idForOtherThanImage" to result.idForOtherThanImage,
                    )
                )
            }
            is CardResult.DescriptionTooLong -> {
                call.respond(
                    HttpStatusCode.PayloadTooLarge, mapOf(
                        "attemptedLength" to result.attemptedLength,
                        "allowedLength" to result.allowedLength
                    )
                )
            }
            is CardResult.TitleTooLong -> {
                call.respond(
                    HttpStatusCode.UnprocessableEntity, mapOf(
                        "attemptedLength" to result.attemptedLength,
                        "allowedLength" to result.allowedLength
                    )
                )
            }
            is CardResult.TitleAndDescriptionTooLong -> {
                call.respond(
                    HttpStatusCode.MultiStatus, mapOf(
                        "attemptedTitleLength" to result.attemptedTitleLength,
                        "allowedTitleLenhtj" to result.allowedTitleLength,
                        "attemptedDescriptionLength" to result.attemptedDescriptionLength,
                        "allowedDescriptionLength" to result.allowedDescriptionLength
                    )
                )
            }
            else -> call.respond(HttpStatusCode.ExpectationFailed)
        }
    }
    post("/cards/delete") {
        val username = call.principal<JWTPrincipal>()?.payload?.getClaim("username")?.asString() ?: return@post
        val cardId = call.receive<String>()

        val result = cards.deleteCard(cardId, username)

        if (result is CardResult.Success) {
            call.respond(HttpStatusCode.OK)
        } else call.respond(HttpStatusCode.ExpectationFailed)
    }
}