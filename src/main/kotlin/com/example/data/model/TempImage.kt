package com.example.data.model

import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.util.*

data class TempImage(
    val _id: String = newId<TempImage>().toString(),
    val image: String,
    val expiresIn: Date = Date(System.currentTimeMillis() + 3600000)
)
