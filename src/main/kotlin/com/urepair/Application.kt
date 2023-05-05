package com.urepair

import at.favre.lib.crypto.bcrypt.BCrypt
import com.urepair.dao.DatabaseFactory
import com.urepair.plugins.configureRouting
import com.urepair.plugins.configureSerialization
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.auth.session
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
import kotlin.time.Duration.Companion.seconds

data class StaffSession(val userID: String)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun Application.module() {
    install(Sessions) {
        val secretSignKey = hex(System.getenv("STAFF_SESSION_SECRET_KEY") ?: throw IllegalStateException("STAFF_SESSION_SECRET_KEY is not set"))
        cookie<StaffSession>("staff_session", SessionStorageMemory()) {
            cookie.path = "/"
            cookie.secure = true
            cookie.httpOnly = true
            transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
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
