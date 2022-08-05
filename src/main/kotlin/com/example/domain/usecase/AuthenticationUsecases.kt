package com.example.domain.usecase

import com.example.domain.model.TokenPair
import com.example.domain.usecase.authentication.*

data class AuthenticationUsecases(
    val getUsernameAndDeleteById: GetUsernameAndDeleteById,
    val deleteByUsername: DeleteByUsername,
    val generateAccessToken: GenerateAccessToken,
    val generateRefreshToken: GenerateRefreshToken,
    val storeRefreshIdUsernamePair: StoreRefreshIdUsernamePair
)