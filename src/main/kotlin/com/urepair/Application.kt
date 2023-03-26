package com.urepair

import com.urepair.dao.DatabaseFactory
import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.urepair.plugins.*
import io.ktor.network.tls.certificates.*
import io.ktor.server.plugins.cors.routing.*
import org.slf4j.*
import java.io.*
import java.util.*

fun loadProperties(fileName: String): Properties {
    val properties = Properties()
    val propertiesFile = FileInputStream(fileName)
    properties.load(propertiesFile)
    propertiesFile.close()
    return properties
}

fun main() {
    val keystoreProperties = loadProperties("keystore.properties")
    val keyAlias = keystoreProperties.getProperty("keyAlias")
    val keyStorePassword = keystoreProperties.getProperty("keyStorePassword")
    val privateKeyPassword = keystoreProperties.getProperty("privateKeyPassword")

    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate(keyAlias) {
            password = keyStorePassword
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, keyStorePassword)

    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        sslConnector(
            keyStore = keyStore,
            keyAlias = keyAlias,
            keyStorePassword = { keyStorePassword.toCharArray() },
            privateKeyPassword = { privateKeyPassword.toCharArray() }) {
            port = 8443
            keyStorePath = keyStoreFile
        }
        module(Application::module)
    }
    embeddedServer(Netty, environment=environment).start(wait = true)
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
