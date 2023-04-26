package com.urepair

import com.urepair.dao.DatabaseFactory
import com.urepair.plugins.configureRouting
import com.urepair.plugins.configureSerialization
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.sessions.*
import io.ktor.util.*
import java.util.Properties

data class StaffSession(val userID: String)

fun loadProperties(fileName: String): Properties {
    val properties = Properties()
    Thread.currentThread().contextClassLoader.getResourceAsStream(fileName).use { inputStream ->
        properties.load(inputStream)
    }
    return properties
}
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun Application.module() {
    install(Sessions) {
        val secretSignKey = hex("6819b57a326945c1968f45236589")
        cookie<StaffSession>("staff_session", SessionStorageMemory()) {
            cookie.path = "/"
            transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
        }
    }
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
        anyHost()
        allowHeader("staff_session")
        exposeHeader("staff_session")
        allowCredentials = true
    }
    install(Authentication) {
        basic("auth-basic") {
            val authenticationProperties = loadProperties("authentication.properties")
            val username = authenticationProperties.getProperty("username")
            val password = authenticationProperties.getProperty("password")
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
