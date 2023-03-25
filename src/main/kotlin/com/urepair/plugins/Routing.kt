package com.urepair.plugins

import io.ktor.server.routing.*
import com.urepair.routes.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        listEquipmentRoute()
        getEquipmentRoute()
        addEquipmentRoute()
        removeEquipmentRoute()
        listIssuesRoute()
        getIssueRoute()
        addIssueRoute()
        removeIssueRoute()
        equipmentQrCode()
    }
}
