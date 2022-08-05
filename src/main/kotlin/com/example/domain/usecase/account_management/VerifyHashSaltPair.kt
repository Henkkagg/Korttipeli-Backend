package com.example.domain.usecase.account_management

import com.example.domain.model.HashPairedWithSalt
import de.mkammerer.argon2.Argon2Advanced
import de.mkammerer.argon2.Argon2Version

class VerifyHashSaltPair(private val argon2: Argon2Advanced) {

    operator fun invoke(correctHashPairedWithSalt: HashPairedWithSalt, unverifiedPassword: String): Boolean {
        val salt = correctHashPairedWithSalt.salt
        val correctHash = correctHashPairedWithSalt.hash
        val hashForUnverifiedPassword = argon2.hashAdvanced(
            10,
            1000,
            4,
            unverifiedPassword.toByteArray(),
            salt,
            128,
            Argon2Version.DEFAULT_VERSION
        ).raw


        return hashForUnverifiedPassword.contentEquals(correctHash)
    }
}