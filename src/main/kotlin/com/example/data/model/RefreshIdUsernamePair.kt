package com.example.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.util.Date

data class RefreshIdUsernamePair(
    @BsonId
    val _id: String = newId<RefreshIdUsernamePair>().toString(),
    val username: String,
    val expiresIn: Date = Date(System.currentTimeMillis() + 3600000)
)