package com.example.data.dto

import org.bson.codecs.pojo.annotations.BsonId

data class RelationshipStatus(
    @BsonId
    val _id: Long,
    val username1: String,
    val username2: String,
    val status: Int
)
