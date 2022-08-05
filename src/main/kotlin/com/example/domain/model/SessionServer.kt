package com.example.domain.model

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class SessionServer(
    @BsonId
    val _id: Id<SessionServer> = newId(),
    val username: String
)
