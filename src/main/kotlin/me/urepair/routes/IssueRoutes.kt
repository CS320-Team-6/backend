package me.urepair.routes

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
import kotlinx.datetime.toJavaLocalDateTime
import me.urepair.dao.dao
import me.urepair.models.Issue
import me.urepair.utilities.sendEmail

fun Route.listIssuesRoute() {
    authenticate("auth-session") {
        get("/issue") {
            call.respond(mapOf("issue_table" to dao.allIssues()))
        }
    }
}
fun Route.getIssueRoute() {
    authenticate("auth-session") {
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
}
fun Route.addIssueRoute() {
    post("/issue") {
        val issue = call.receive<Issue>()
        val newIssue = dao.addNewIssue(
            equipmentId = issue.equipmentId,
            description = issue.description,
            status = issue.status,
            dateReported = issue.dateReported.toJavaLocalDateTime(),
            priority = issue.priority,
            assignedTo = issue.assignedTo,
            dateResolved = issue.dateResolved?.toJavaLocalDateTime(),
            resolutionDetails = issue.resolutionDetails,
            notes = issue.notes,
        )
        val equipmentName = dao.equipment(issue.equipmentId)?.name
        val staffEmail = "jwordell@umass.edu"
        val subject = "New ticket created"
        val message = "A new ticket has been created for $equipmentName on urepair with priority ${issue.priority}." +
            "A description of the issue: ${issue.description}"

        newIssue?.let {
            if (!dao.updateIssueCount(issue.equipmentId)) {
                dao.addNewIssueCount(issue.equipmentId)
            }
            sendEmail(staffEmail, subject, message)
            call.respondText("${it.id}", status = HttpStatusCode.Created)
        } ?: call.respond(HttpStatusCode.InternalServerError)
    }
}
fun Route.editIssueRoute() {
    authenticate("auth-session") {
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
                issue.dateReported.toJavaLocalDateTime(),
                issue.priority,
                issue.description,
                issue.assignedTo,
                issue.dateResolved?.toJavaLocalDateTime(),
                issue.resolutionDetails,
                issue.notes,
            )
            editedIssue.let {
                if (editedIssue) {
                    if (issue.assignedTo != null && issue.status == Issue.Status.IN_PROGRESS) {
                        val equipmentName = dao.equipment(issue.equipmentId)?.name
                        val subject = "New ticket created for $equipmentName"
                        val message =
                            "A new ticket has been created for $equipmentName on urepair with priority ${issue.priority}." +
                                "A description of the issue: ${issue.description}"
                        sendEmail(issue.assignedTo, subject, message)
                    }
                    call.respondText("Issue edited correctly", status = HttpStatusCode.Accepted)
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}
fun Route.removeIssueRoute() {
    authenticate("auth-session") {
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
    authenticate("auth-session") {
        get("/issue/count") {
            call.respond(mapOf("issue_count_table" to dao.allIssueCounts()))
        }
    }
}
fun Route.getIssueCountRoute() {
    authenticate("auth-session") {
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
}
fun Route.removeIssueCountRoute() {
    authenticate("auth-session") {
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