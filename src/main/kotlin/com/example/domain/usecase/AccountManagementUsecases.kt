package com.example.domain.usecase

import com.example.domain.usecase.account_management.*

data class AccountManagementUsecases(
    val verifyUsernameLegality: VerifyUsernameLegality,
    val generateHashSaltPair: GenerateHashSaltPair,
    val verifyHashSaltPair: VerifyHashSaltPair,
    val createNewAccount: CreateNewAccount,
    val getHashSaltPair: GetHashSaltPair,
    val checkIfUsernameExists: CheckIfUsernameExists
)


sealed class AccountManagementResult {
    object Success : AccountManagementResult()
    object DatabaseError : AccountManagementResult()
    object UsernameTaken : AccountManagementResult()
    object IllegalCharacters : AccountManagementResult()
    object PasswordWrong : AccountManagementResult()
    object UsernameNotFound : AccountManagementResult()
}
