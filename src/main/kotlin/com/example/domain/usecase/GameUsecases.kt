package com.example.domain.usecase

import com.example.domain.usecase.game.CreateGame
import com.example.domain.usecase.game.GetGameId
import com.example.domain.usecase.game.JoinGame
import com.example.domain.usecase.game.UpdateAuthorizedCards

data class GameUsecases(
    val createGame: CreateGame,
    val joinGame: JoinGame,
    val getGameId: GetGameId,
    val updateAuthorizedCards: UpdateAuthorizedCards
)
