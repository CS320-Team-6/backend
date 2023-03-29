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
import java.io.FileInputStream
import java.util.Properties

fun loadProperties(fileName: String): Properties {
    val properties = Properties()
    val propertiesFile = FileInputStream(fileName)
    properties.load(propertiesFile)
    propertiesFile.close()
    return properties
}
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun Application.module() {
    install(CORS) {
        anyHost()
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
