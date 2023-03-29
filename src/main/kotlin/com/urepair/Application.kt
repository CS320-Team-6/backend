package com.urepair

import com.urepair.dao.DatabaseFactory
import com.urepair.plugins.configureRouting
import com.urepair.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.routing.*
import java.util.Properties

fun loadProperties(fileName: String): Properties {
    val properties = Properties()
    Thread.currentThread().contextClassLoader.getResourceAsStream(fileName).use { inputStream ->
        properties.load(inputStream)
    }
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
    routing {
//        singlePageApplication {
//            useResources = true
//            filesPath = "frontend"
//            defaultPage = "main.html"
//            ignoreFiles { it.endsWith(".txt") }
//        }
        singlePageApplication {
            react("react-app")
        }
    }
    configureRouting()
}
