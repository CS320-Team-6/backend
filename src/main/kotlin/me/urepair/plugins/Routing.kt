package me.urepair.plugins

import io.ktor.server.application.Application
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.routing.routing
import me.urepair.routes.addEquipmentRoute
import me.urepair.routes.addIssueCountRoute
import me.urepair.routes.addIssueRoute
import me.urepair.routes.addUserRoute
import me.urepair.routes.editEquipmentRoute
import me.urepair.routes.editIssueRoute
import me.urepair.routes.editUserRoute
import me.urepair.routes.equipmentQrCode
import me.urepair.routes.getEquipmentRoute
import me.urepair.routes.getIssueCountRoute
import me.urepair.routes.getIssueRoute
import me.urepair.routes.getUserRoute
import me.urepair.routes.listEquipmentRoute
import me.urepair.routes.listIssuesCountRoute
import me.urepair.routes.listIssuesRoute
import me.urepair.routes.listUsersRoute
import me.urepair.routes.removeEquipmentRoute
import me.urepair.routes.removeIssueCountRoute
import me.urepair.routes.removeIssueRoute
import me.urepair.routes.removeUserRoute
import me.urepair.routes.updateLogin
import me.urepair.routes.userLogin

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

        userLogin()
        updateLogin()
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
