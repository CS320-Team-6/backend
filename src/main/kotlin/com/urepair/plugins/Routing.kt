package com.urepair.plugins

import com.urepair.routes.addEquipmentRoute
import com.urepair.routes.addIssueRoute
import com.urepair.routes.addUserRoute
import com.urepair.routes.equipmentQrCode
import com.urepair.routes.getEquipmentRoute
import com.urepair.routes.getIssueRoute
import com.urepair.routes.getUserRoute
import com.urepair.routes.listEquipmentRoute
import com.urepair.routes.listIssuesRoute
import com.urepair.routes.listUsersRoute
import com.urepair.routes.removeEquipmentRoute
import com.urepair.routes.removeIssueRoute
import com.urepair.routes.removeUserRoute
import io.ktor.server.application.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

// import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Welcome to urepair!\n To get started, try navigating to /equipment")
        }
        listEquipmentRoute()
        getEquipmentRoute()
        addEquipmentRoute()
        removeEquipmentRoute()
        equipmentQrCode()

        listIssuesRoute()
        getIssueRoute()
        addIssueRoute()
        removeIssueRoute()

        listUsersRoute()
        getUserRoute()
        addUserRoute()
        removeUserRoute()
    }
}
