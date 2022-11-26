package com.example.domain.logicflow

import com.example.domain.model.AccountClient
import com.example.data.dto.AccountServer
import com.example.domain.usecase.AccountManagementResult
import com.example.domain.usecase.AccountManagementUsecases
import org.koin.java.KoinJavaComponent.inject

class Registration {

    suspend fun execute(accountClient: AccountClient): AccountManagementResult {
        val accountManagement: AccountManagementUsecases by inject(AccountManagementUsecases::class.java)

        val username = accountClient.username.trim()
        val email = accountClient.email.trim()

        if (!accountManagement.verifyUsernameLegality(username)) {
            return AccountManagementResult.IllegalCharacters
        }

        val (hash, salt) = accountManagement.generateHashSaltPair(accountClient.password)

        val accountServer = AccountServer(
            username = username,
            email = email,
            hash = hash,
            salt = salt
        )

        val result = accountManagement.createNewAccount(accountServer)
        return if (result) AccountManagementResult.Success else AccountManagementResult.UsernameTaken
    }
}