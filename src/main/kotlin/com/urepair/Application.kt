package com.urepair

import com.urepair.dao.DatabaseFactory
import com.urepair.plugins.configureRouting
import com.urepair.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.plugins.cors.routing.CORS

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun Application.module() {
    install(CORS) {
        anyHost()
    }
    install(Authentication) {
        basic("auth-basic") {
            val username = "team6"
            val password = "cs320Team6"
            realm = "Access to the '/' path"
            validate { credentials ->
                if (credentials.name == username && credentials.password == password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
    DatabaseFactory.init()
    configureSerialization()
    configureRouting()
}
