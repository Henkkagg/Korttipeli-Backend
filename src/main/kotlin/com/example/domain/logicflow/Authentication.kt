package com.example.domain.logicflow

import com.example.data.model.RefreshIdUsernamePair
import com.example.domain.model.Credidentials
import com.example.domain.model.TokenPair
import com.example.domain.usecase.AccountManagementResult
import com.example.domain.usecase.AccountManagementUsecases
import com.example.domain.usecase.AuthenticationUsecases
import org.koin.java.KoinJavaComponent.inject
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import org.litote.kmongo.toId

class Authentication {
    private val authentication: AuthenticationUsecases by inject(AuthenticationUsecases::class.java)

    suspend fun checkCredidentials(credidentials: Credidentials): AccountManagementResult {
        val accountManagement: AccountManagementUsecases by inject(AccountManagementUsecases::class.java)

        val username = credidentials.username.trim()
        val unverifiedPassword = credidentials.password

        val correctHashPairedWithSalt =
            accountManagement.getHashSaltPair(username) ?: return AccountManagementResult.UsernameNotFound

        val result = accountManagement.verifyHashSaltPair(correctHashPairedWithSalt, unverifiedPassword)

        return if (result) AccountManagementResult.Success else AccountManagementResult.PasswordWrong
    }

    suspend fun generateTokenPairByCredidentials(credidentials: Credidentials): TokenPair? {
        val username = credidentials.username.trim()
        authentication.deleteByUsername(username)

        return tokenPairGenerator(username)
    }

    suspend fun generateTokenPairByRefresh(id: String, username: String): TokenPair? {
        val usernameForTokenId = authentication.getUsernameAndDeleteById(id)

        //Security measure. Revoke valid refresh token if somebody tries to reuse old refresh token
        if (usernameForTokenId == null) {
            authentication.deleteByUsername(username)
            println("Warning, hackers detected!!!")
            return null
        }

        return tokenPairGenerator(username)
    }

    private suspend fun tokenPairGenerator(username: String): TokenPair? {
        val id = newId<RefreshIdUsernamePair>().toString()

        val accessToken = authentication.generateAccessToken(username)
        val refreshToken = authentication.generateRefreshToken(id, username)
        val tokenPair = TokenPair(accessToken, refreshToken)

        val refreshIdUsernamePair = RefreshIdUsernamePair(id, username)

        val dbResult = authentication.storeRefreshIdUsernamePair(refreshIdUsernamePair)

        return if (dbResult) tokenPair else null
    }
}