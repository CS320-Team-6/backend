package com.urepair.routes

import com.urepair.StaffSession
import com.urepair.dao.dao
import com.urepair.models.User
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set

fun Route.userLogin() {
    authenticate("auth-basic") {
        post("/login") {
            val principal = call.authentication.principal<UserIdPrincipal>()
            if (principal != null) {
                call.sessions.set(StaffSession(principal.name))
                call.respondText("Logged in as ${principal.name}")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }

        delete("/logout") {
            val session = call.sessions.get<StaffSession>()
            if (session != null) {
                call.sessions.clear<StaffSession>()
                call.respondText("Logged out")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Not logged in")
            }
        }
    }
}
fun Route.listUsersRoute() {
    authenticate("auth-basic") {
        get("/user") {
            call.respond(mapOf("user_table" to dao.allUsers()))
        }
    }
}

fun Route.getUserRoute() {
    authenticate("auth-basic") {
        get("/user/{email?}") {
            val email = call.parameters["email"] ?: return@get call.respondText(
                "Missing email",
                status = HttpStatusCode.BadRequest,
            )
            val user = dao.user(email) ?: return@get call.respondText(
                "No user with id $email",
                status = HttpStatusCode.NotFound,
            )
            call.respond(user)
        }
    }
}

fun Route.addUserRoute() {
    post("/user") {
        val user = call.receive<User>()
        dao.addNewUser(
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            role = user.role,
        )
        call.respondText("User stored correctly", status = HttpStatusCode.Created)
    }
}

fun Route.editUserRoute() {
    post("/user/{email?}") {
        val email = call.parameters["email"] ?: return@post call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest,
        )
        val user = call.receive<User>()
        val editedUser = dao.editUser(
            user.firstName,
            user.lastName,
            email,
            user.role,
        )
        editedUser.let {
            if (editedUser) {
                call.respondText("User edited correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}

fun Route.removeUserRoute() {
    authenticate("auth-basic") {
        delete("/user/{email?}") {
            val email = call.parameters["email"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (dao.deleteUser(email)) {
                call.respondText("User removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
