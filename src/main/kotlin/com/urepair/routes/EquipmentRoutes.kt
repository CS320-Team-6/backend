package com.urepair.routes

import com.urepair.dao.dao
import com.urepair.models.Equipment
import io.github.g0dkar.qrcode.QRCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


fun Route.listEquipmentRoute() {
    get ("/equipment"){
        call.respond(mapOf("equipment_table" to dao.allEquipment()))
    }
}

fun Route.getEquipmentRoute() {
    get("/equipment/{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val equip = dao.equipment(id.toInt()) ?: return@get call.respondText(
            "No equipment with id $id",
            status = HttpStatusCode.NotFound
        )
        call.respond(equip)
    }
}

fun Route.addEquipmentRoute() {
    post ("/equipment"){
        val equip = call.receive<Equipment>()
        dao.addNewEquipment(
            name = equip.name,
            dateInstalled = equip.dateInstalled,
            equipmentType = equip.equipmentType,
            location = equip.location,
            manufacturer = equip.manufacturer,
            model = equip.model,
            serialNumber = equip.serialNumber,
            lastMaintenanceDate = equip.lastMaintenanceDate,
        )
        call.respondText("Equipment stored correctly", status = HttpStatusCode.Created)
    }
}

fun Route.removeEquipmentRoute() {
    delete("/equipment/{id?}") {
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        if (dao.deleteEquipment(id.toInt())) {
            call.respondText("Equipment removed correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }
}

fun Route.equipmentQrCode() {
    static("/qr") {
        files("images/qr")
    }
    get("/equipment/qr/{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest
        )
        dao.equipment(id.toInt()) ?: return@get call.respondText(
            "No equipment with id $id",
            status = HttpStatusCode.NotFound
        )
        val fileName = "images/qr/$id.png"
        val file = File(fileName)
        if(!file.exists()) {
            withContext(Dispatchers.IO) {
                file.parentFile.mkdirs()
                FileOutputStream(fileName).use {
                    QRCode("/equipment/$id")
                        .render()
                        .writeImage(it)
                }
            }
        }
        call.respondRedirect("/qr/$id.png")
    }
}
