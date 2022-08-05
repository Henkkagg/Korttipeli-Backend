package com.example.domain.usecase.account_management

class VerifyUsernameLegality {

    operator fun invoke(username: String): Boolean {
        val filteredName = username.filter { character -> character.isLetter() || character.isDigit() }
        return (filteredName == username)
    }
}