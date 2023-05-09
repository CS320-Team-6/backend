package me.urepair.routes

import io.github.g0dkar.qrcode.QRCode
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.http.content.staticFiles
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.urepair.dao.dao
import me.urepair.models.Equipment
import java.io.File
import java.io.FileOutputStream

fun Route.listEquipmentRoute() {
    get("/equipment") {
        call.respond(mapOf("equipment_table" to dao.allEquipment()))
    }
}

fun Route.getEquipmentRoute() {
    authenticate("auth-session") {
        get("/equipment/{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest,
            )
            val equip = dao.equipment(id.toInt()) ?: return@get call.respondText(
                "No equipment with id $id",
                status = HttpStatusCode.NotFound,
            )
            call.respond(equip)
        }
    }
}

fun Route.addEquipmentRoute() {
    authenticate("auth-session") {
        post("/equipment") {
            val equipment = call.receive<Equipment>()
            val newEquipment = dao.addNewEquipment(
                name = equipment.name,
                dateInstalled = equipment.dateInstalled,
                equipmentType = equipment.equipmentType,
                location = equipment.location,
                manufacturer = equipment.manufacturer,
                model = equipment.model,
                serialNumber = equipment.serialNumber,
                lastMaintenanceDate = equipment.lastMaintenanceDate,
            )
            newEquipment?.let {
                call.respondText("${it.id}", status = HttpStatusCode.Created)
            } ?: call.respond(HttpStatusCode.InternalServerError)
        }
    }
}

fun Route.editEquipmentRoute() {
    authenticate("auth-session") {
        post("/equipment/{id?}") {
            val id = call.parameters["id"] ?: return@post call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest,
            )
            val equipment = call.receive<Equipment>()
            val editedEquipment = dao.editEquipment(
                id.toInt(),
                equipment.name,
                equipment.equipmentType,
                equipment.manufacturer,
                equipment.model,
                equipment.serialNumber,
                equipment.location,
                equipment.dateInstalled,
                equipment.lastMaintenanceDate,
            )
            editedEquipment.let {
                if (editedEquipment) {
                    call.respondText("Equipment edited correctly", status = HttpStatusCode.Accepted)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}
fun Route.removeEquipmentRoute() {
    authenticate("auth-session") {
        delete("/equipment/{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (dao.deleteEquipment(id.toInt())) {
                call.respondText("Equipment removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
fun Route.equipmentQrCode() {
    staticFiles("/qr", File("images/qr"))
    authenticate("auth-session") {
        get("/equipment/qr/{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest,
            )
            dao.equipment(id.toInt()) ?: return@get call.respondText(
                "No equipment with id $id",
                status = HttpStatusCode.NotFound,
            )
            val fileName = "images/qr/$id.png"
            val file = File(fileName)
            if (!file.exists()) {
                withContext(Dispatchers.IO) {
                    file.parentFile.mkdirs()
                    FileOutputStream(fileName).use {
                        QRCode("https://urepair.me/?id=$id")
                            .render()
                            .writeImage(it)
                    }
                }
            }
            call.respondRedirect("/qr/$id.png")
        }
    }
}
