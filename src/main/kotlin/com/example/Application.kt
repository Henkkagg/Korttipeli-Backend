package com.example

import io.ktor.server.application.*
import com.example.plugins.*
import org.koin.ktor.ext.Koin

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureKoin()
    configureAuthentication()
    configureSockets()
    configureRouting()
    configureSerialization()
    configureMonitoring()

}
