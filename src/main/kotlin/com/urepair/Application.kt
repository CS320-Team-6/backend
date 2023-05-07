package com.urepair

import at.favre.lib.crypto.bcrypt.BCrypt
import com.urepair.dao.DatabaseFactory
import com.urepair.plugins.configureRouting
import com.urepair.plugins.configureSerialization
import com.urepair.utilities.getSecret
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.auth.session
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.hsts.HSTS
import io.ktor.server.plugins.httpsredirect.HttpsRedirect
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.response.respond
import io.ktor.server.sessions.SessionStorageMemory
import io.ktor.server.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.util.hex
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.time.Duration.Companion.seconds

data class StaffSession(val userID: String)

fun main() {
    val awsSecret = getSecret("urepair/jks")
    val keyStorePassword = awsSecret["KEY_STORE_PASSWORD"]?.jsonPrimitive.toString()
    val keyStoreAlias = awsSecret["KEY_STORE_ALIAS"]?.jsonPrimitive.toString()
    val keyStoreFile = File("urepair_me.jks")
    val keyStore = buildKeyStore {
        certificate(keyStoreAlias) {
            password = keyStorePassword
        }
    }
    keyStore.saveToFile(keyStoreFile, keyStorePassword)

    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = System.getenv("PORT")?.toInt() ?: 5000
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = System.getenv("KEY_STORE_ALIAS"),
            keyStorePassword = { keyStorePassword.toCharArray() },
            privateKeyPassword = { keyStorePassword.toCharArray() },
        ) {
            port = 8433
            keyStorePath = keyStoreFile
        }
        module(Application::module) // Update this line to use the appropriate module
    }

    embeddedServer(Netty, environment).start(wait = true)
}

fun Application.module() {
    install(Sessions) {
        val secretSignKey = hex(System.getenv("STAFF_SESSION_SECRET_KEY") ?: throw IllegalStateException("STAFF_SESSION_SECRET_KEY is not set"))
        cookie<StaffSession>("staff_session", SessionStorageMemory()) {
            cookie.path = "/"
            cookie.secure = true
            cookie.httpOnly = true
            transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
            cookie.maxAgeInSeconds = 1800
        }
    }
    install(RateLimit) {
        global {
            rateLimiter(limit = 30, refillPeriod = 60.seconds)
        }
    }
    install(HSTS) {
        maxAgeInSeconds = 15550000
    }
    install(HttpsRedirect) {
        sslPort = 8443
        permanentRedirect = true
    }
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        anyHost()
        allowHeader("staff_session")
        exposeHeader("staff_session")
        allowCredentials = true
    }
    install(Authentication) {
        basic("auth-basic") {
            val username = System.getenv("STAFF_UNAME")
            val hashedPassword = System.getenv("STAFF_SECRET")
            realm = "Access to the '/' path"
            validate { credentials ->
                val passwordVerificationResult = BCrypt.verifyer().verify(credentials.password.toCharArray(), hashedPassword)
                if (credentials.name == username && passwordVerificationResult.verified) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
        session<StaffSession>("auth-session") {
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
            validate { session: StaffSession ->
                if (session.userID.isNotBlank()) {
                    UserIdPrincipal(session.userID)
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
