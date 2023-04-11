package com.urepair.routes

import com.urepair.dao.dao
import com.urepair.models.Issue
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.listIssuesRoute() {
    get("/issue") {
        call.respond(mapOf("issue_table" to dao.allIssues()))
    }
}
fun Route.getIssueRoute() {
    // remove authentication for now
    get("/issue/{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest,
        )
        val issue = dao.issue(id.toInt()) ?: return@get call.respondText(
            "No issue with id $id",
            status = HttpStatusCode.NotFound,
        )
        call.respond(issue)
    }
}

fun Route.addIssueRoute() {
    post("/issue") {
        val issue = call.receive<Issue>()
        val newIssue = dao.addNewIssue(
            equipmentId = issue.equipmentId,
            description = issue.description,
            status = issue.status,
            dateReported = issue.dateReported,
            priority = issue.priority,
            assignedTo = issue.assignedTo,
            dateResolved = issue.dateResolved,
            resolutionDetails = issue.resolutionDetails,
            notes = issue.notes,
        )
        newIssue?.let {
            if (!dao.updateIssueCount(issue.equipmentId)) {
                dao.addNewIssueCount(issue.equipmentId)
            }
            call.respondText("${it.id}", status = HttpStatusCode.Created)
        } ?: call.respond(HttpStatusCode.InternalServerError)
    }
}
fun Route.editIssueRoute() {
    post("/issue/{id?}") {
        val id = call.parameters["id"] ?: return@post call.respondText(
            "Missing id",
            status = HttpStatusCode.BadRequest,
        )
        val issue = call.receive<Issue>()
        val editedIssue = dao.editIssue(
            id.toInt(),
            issue.equipmentId,
            issue.status ?: Issue.Status.NEW,
            issue.dateReported,
            issue.priority,
            issue.description,
            issue.assignedTo,
            issue.dateResolved,
            issue.resolutionDetails,
            issue.notes,
        )
        editedIssue.let {
            if (editedIssue) {
                call.respondText("Issue edited correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}

fun Route.removeIssueRoute() {
    authenticate("auth-basic") {
        delete("/issue/{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (dao.deleteIssue(id.toInt())) {
                call.respondText("Issue removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}

fun Route.listIssuesCountRoute() {
    authenticate("auth-basic") {
        get("/issue/count") {
            call.respond(mapOf("issue_count_table" to dao.allIssueCounts()))
        }
    }
}
fun Route.getIssueCountRoute() {
    // remove authentication for now
    get("/issue/count/{equipment_id?}") {
        val equipmentId = call.parameters["equipment_id"] ?: return@get call.respondText(
            "Missing equipment id",
            status = HttpStatusCode.BadRequest,
        )
        val issueCount = dao.issueCount(equipmentId.toInt()) ?: return@get call.respondText(
            "No issues with equipment id $equipmentId",
            status = HttpStatusCode.NotFound,
        )
        call.respond(issueCount)
    }
}

fun Route.addIssueCountRoute() {
    post("/issue") {
        val issue = call.receive<Issue>()
        val newIssue = dao.addNewIssue(
            equipmentId = issue.equipmentId,
            description = issue.description,
            status = issue.status,
            dateReported = issue.dateReported,
            priority = issue.priority,
            assignedTo = issue.assignedTo,
            dateResolved = issue.dateResolved,
            resolutionDetails = issue.resolutionDetails,
            notes = issue.notes,
        )
        newIssue?.let {
            call.respondText("${it.id}", status = HttpStatusCode.Created)
        } ?: call.respond(HttpStatusCode.InternalServerError)
    }
}

fun Route.removeIssueCountRoute() {
    authenticate("auth-basic") {
        delete("/issue/count/{equipment_id?}") {
            val equipmentId = call.parameters["equipment_id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (dao.deleteIssue(equipmentId.toInt())) {
                call.respondText("Issue count removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
