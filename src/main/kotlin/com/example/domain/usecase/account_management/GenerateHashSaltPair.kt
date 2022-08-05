package com.example.domain.usecase.account_management

import com.example.domain.model.HashPairedWithSalt
import de.mkammerer.argon2.Argon2Advanced
import de.mkammerer.argon2.Argon2Version

class GenerateHashSaltPair(private val argon2: Argon2Advanced) {

    operator fun invoke(password: String): HashPairedWithSalt {
        val salt = argon2.generateSalt()
        val hash = argon2.hashAdvanced(
            10,
            1000,
            4,
            password.toByteArray(),
            salt,
            128,
            Argon2Version.DEFAULT_VERSION
        ).raw

        return HashPairedWithSalt(hash, salt)
    }
}