package com.urepair

import com.urepair.dao.DatabaseFactory
import com.urepair.plugins.configureRouting
import com.urepair.plugins.configureSerialization
import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.cors.routing.CORS
import org.slf4j.LoggerFactory
import java.security.KeyStore
import java.util.Properties

fun loadProperties(fileName: String): Properties {
    val properties = Properties()
    Thread.currentThread().contextClassLoader.getResourceAsStream(fileName).use { inputStream ->
        properties.load(inputStream)
    }
    return properties
}
fun main() {
    val keystoreProperties = loadProperties("keystore.properties")
    val keyAlias = keystoreProperties.getProperty("keyAlias")
    val keyStorePassword = keystoreProperties.getProperty("keyStorePassword")
    val privateKeyPassword = keystoreProperties.getProperty("privateKeyPassword")

    val keyStoreFile = KeyStore.getInstance(KeyStore.getDefaultType())
    Thread.currentThread().contextClassLoader.getResourceAsStream("keyStore").use { inputStream ->
        keyStoreFile.load(inputStream, keyStorePassword.toCharArray())
    }
    val keyStore = buildKeyStore {
        certificate(keyAlias) {
            password = keyStorePassword
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }

    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = (System.getenv("PORT") ?: "5000").toInt()
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = keyAlias,
            keyStorePassword = { keyStorePassword.toCharArray() },
            privateKeyPassword = { privateKeyPassword.toCharArray() },
        ) {
            port = 8433
        }
        module(Application::module)
    }
    embeddedServer(Netty, environment = environment).start(wait = true)
}
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
