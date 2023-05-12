package me.urepair.routes

import at.favre.lib.crypto.bcrypt.BCrypt
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
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import me.urepair.StaffSession
import me.urepair.dao.dao
import me.urepair.models.Email
import me.urepair.models.PasswordRequest
import me.urepair.models.ResetPassword
import me.urepair.models.User
import me.urepair.secrets.HashedStaffSecret
import me.urepair.secrets.StaffSecret
import me.urepair.secrets.getStaffSecret
import me.urepair.secrets.updateStaffSecret
import me.urepair.utilities.sendEmail
import java.time.LocalDateTime
import java.util.UUID

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
                call.respond(HttpStatusCode.Forbidden, "Not logged in")
            }
        }
    }
}
fun Route.updateLogin() {
    authenticate("auth-session") {
        post("/login/update") {
            try {
                val input = call.receive<StaffSecret>()
                val hashedPassword = BCrypt.withDefaults().hashToString(10, input.staffSecret.toCharArray())
                val newStaffSecret = HashedStaffSecret(hashedPassword, input.staffEmail)
                updateStaffSecret("urepair/staffLogin", newStaffSecret)
                call.respondText("Success")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid input")
            }
        }
    }
}
fun Route.forgottenPassword() {
    post("/forgot-password") {
        try {
            val input = call.receive<Email>()
            val email = input.email

            // Generate a unique token and store it in the database with an expiration timestamp
            val token = UUID.randomUUID().toString()
            val expiresAt = LocalDateTime.now().plusMinutes(15).toKotlinLocalDateTime()
            val resetRequest = PasswordRequest(email, token, expiresAt)

            val resetLink = "https://urepair.me/?token=$token"
            if (email == getStaffSecret("urepair/staffLogin").staffEmail) {
                dao.addPasswordRequest(
                    resetRequest.email,
                    resetRequest.token,
                    resetRequest.expiresAt.toJavaLocalDateTime(),
                )
                sendEmail(
                    email,
                    "Password Reset Request",
                    "Click the following link to reset your password: $resetLink",
                )
            }
            call.respond(HttpStatusCode.OK, "A password reset link has been sent to your email")
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid input")
        }
    }
}
fun Route.resetPassword() {
    post("/reset-password") {
        try {
            val input = call.receive<ResetPassword>()
            val token = input.token
            val newPassword = input.newPassword

            val resetRequest = dao.getPasswordRequestToken(token)
            if (resetRequest == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or expired token")
                return@post
            }

            val email = resetRequest.email
            val hashedPassword = BCrypt.withDefaults().hashToString(10, newPassword.toCharArray())
            val newStaffSecret = HashedStaffSecret(hashedPassword, email)
            updateStaffSecret("urepair/staffLogin", newStaffSecret)

            dao.deletePasswordRequest(token)
            call.respond(HttpStatusCode.OK, "Password has been reset successfully")
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid input")
        }
    }
}
fun Route.listUsersRoute() {
    authenticate("auth-session") {
        get("/user") {
            call.respond(mapOf("user_table" to dao.allUsers()))
        }
    }
}
fun Route.getUserRoute() {
    authenticate("auth-session") {
        get("/user/{email?}") {
            val email = call.parameters["email"] ?: return@get call.respondText(
                "Missing email",
                status = HttpStatusCode.BadRequest,
            )
            val user = dao.user(email) ?: return@get call.respondText(
                "No user with email $email",
                status = HttpStatusCode.NotFound,
            )
            call.respond(user)
        }
    }
}
fun Route.addUserRoute() {
    authenticate("auth-session") {
        post("/user") {
            try {
                val user = call.receive<User>()
                val newUser = dao.addNewUser(
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email,
                    role = user.role,
                )
                newUser.let {
                    if (newUser != null) {
                        call.respondText("User stored correctly", status = HttpStatusCode.Created)
                    } else {
                        call.respondText("Failed to create user", status = HttpStatusCode.InternalServerError)
                    }
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid input")
            }
        }
    }
}
fun Route.editUserRoute() {
    authenticate("auth-session") {
        post("/user/{email?}") {
            val email = call.parameters["email"] ?: return@post call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest,
            )
            try {
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
                        call.respondText("Failed to edit user", status = HttpStatusCode.InternalServerError)
                    }
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid input")
            }
        }
    }
}
fun Route.removeUserRoute() {
    authenticate("auth-session") {
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
