package com.urepair.plugins

import com.urepair.routes.addEquipmentRoute
import com.urepair.routes.addIssueCountRoute
import com.urepair.routes.addIssueRoute
import com.urepair.routes.addUserRoute
import com.urepair.routes.editEquipmentRoute
import com.urepair.routes.editIssueRoute
import com.urepair.routes.editUserRoute
import com.urepair.routes.equipmentQrCode
import com.urepair.routes.getEquipmentRoute
import com.urepair.routes.getIssueCountRoute
import com.urepair.routes.getIssueRoute
import com.urepair.routes.getUserRoute
import com.urepair.routes.listEquipmentRoute
import com.urepair.routes.listIssuesCountRoute
import com.urepair.routes.listIssuesRoute
import com.urepair.routes.listUsersRoute
import com.urepair.routes.removeEquipmentRoute
import com.urepair.routes.removeIssueCountRoute
import com.urepair.routes.removeIssueRoute
import com.urepair.routes.removeUserRoute
import io.ktor.server.application.Application
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        singlePageApplication {
            useResources = true
            react("react-app")
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
