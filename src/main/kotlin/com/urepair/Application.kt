package com.urepair

import com.urepair.dao.DatabaseFactory
import io.ktor.server.application.*
import com.urepair.plugins.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.cors.routing.*

//fun loadProperties(fileName: String): Properties {
//    val properties = Properties()
//    val propertiesFile = FileInputStream(fileName)
//    properties.load(propertiesFile)
//    propertiesFile.close()
//    return properties
//}
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
