package com.urepair.routes

import com.urepair.dao.dao
import com.urepair.models.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.listUsersRoute() {
    get ("/user"){
        call.respond(mapOf("user_table" to dao.allUsers()))
    }
}
fun Route.getUserRoute() {
    get("/user/{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val user = dao.user(id.toInt()) ?: return@get call.respondText(
            "No user with id $id",
            status = HttpStatusCode.NotFound
        )
        call.respond(user)
    }
}

fun Route.addUserRoute() {
    post ("/user"){
        val user = call.receive<User>()
        dao.addNewUser(
            name = user.name,
            email = user.email,
            role = user.role,
        )
        call.respondText("User stored correctly", status = HttpStatusCode.Created)
    }
}

fun Route.removeUserRoute() {
    delete("/user/{id?}") {
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        if (dao.deleteUser(id.toInt())) {
            call.respondText("User removed correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }
}
