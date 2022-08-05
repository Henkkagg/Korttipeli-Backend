package com.example.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class AccountServer(
    @BsonId
    val _id: Id<AccountServer> = newId(),
    val username: String,
    val cardsOwned: List<String> = emptyList(),
    val email: String,
    val hash: ByteArray,
    val salt: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountServer

        if (_id != other._id) return false
        if (username != other.username) return false
        if (!salt.contentEquals(other.salt)) return false
        if (!hash.contentEquals(other.hash)) return false
        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + salt.contentHashCode()
        result = 31 * result + hash.contentHashCode()
        result = 31 * result + email.hashCode()
        return result
    }
}