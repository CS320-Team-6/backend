package com.urepair.plugins

import com.urepair.routes.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        singlePageApplication {
            useResources = true
            react("react-app")
        }
        get("/.well-known/pki-validation/56E8D287CD70A53033EF467DD258ED8C.txt") {
            val text = javaClass.classLoader.getResourceAsStream("56E8D287CD70A53033EF467DD258ED8C.txt")?.bufferedReader().use { it?.readText() }
            if (text != null) {
                call.respondText(text, ContentType.Text.Plain)
            } else {
                call.respond(HttpStatusCode.NotFound, "txt file not found in resources")
            }
        }

        listEquipmentRoute()
        getEquipmentRoute()
        addEquipmentRoute()
        editEquipmentRoute()
        removeEquipmentRoute()
        equipmentQrCode()

        listIssuesRoute()
        getIssueRoute()
        addIssueRoute()
        editIssueRoute()
        removeIssueRoute()

        userLogin()
        listUsersRoute()
        getUserRoute()
        addUserRoute()
        editUserRoute()
        removeUserRoute()

        listIssuesCountRoute()
        getIssueCountRoute()
        addIssueCountRoute()
        removeIssueCountRoute()
    }
}
