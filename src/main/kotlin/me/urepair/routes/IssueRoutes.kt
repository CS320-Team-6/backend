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

private fun buildSupportTicketEmailHtml(ticketMachine: String, ticketDescription: String): String {
    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
        body {
          font-family: Arial, sans-serif;
          margin: 0;
          padding: 0;
          background-color: #f4f4f4;
        }
        .container {
          width: 100%;
          max-width: 600px;
          margin: 0 auto;
        }
        .header,
        .footer {
          background-color: #800000;
          color: #fff;
          text-align: center;
          padding: 20px;
        }
        .content {
          background-color: #fff;
          padding: 20px;
        }
        h1 {
          font-size: 24px;
          margin-bottom: 10px;
        }
        p {
          font-size: 16px;
          line-height: 1.5;
        }
        .footer p {
          font-size: 14px;
          margin: 0;
        }
        .button {
          display: inline-block;
          background-color: #800000;
          color: #fff;
          text-decoration: none;
          padding: 10px 20px;
          border-radius: 4px;
          font-size: 16px;
        }
        @media only screen and (max-width: 600px) {
          .container {
            width: 100%;
            padding: 10px;
          }
        }
        </style>
        </head>
        <body>
        <div class="container">
          <div class="header">
            <h1>URepair</h1>
          </div>
          <div class="content">
            <h1>Support Ticket Created</h1>
            <p>Hello Staff,</p>
            <p>We have received a support ticket.</p>
            <p><strong>Ticket Details:</strong></p>
            <p>Machine: $ticketMachine</p>
            <p>Description: $ticketDescription</p>
            <br>
            <p><a href="https://www.urepair.me/" class="button">View Portal</a></p>
          </div>
          <div class="footer">
            <p>&copy; 2023 Urepair. All rights reserved.</p>
          </div>
        </div>
        </body>
        </html>
    """.trimIndent()
}

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
        try {
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
            val staffEmail = "staff@urepair.me"
            val subject = "New ticket created"
            val message =
                "A new ticket has been created for $equipmentName on urepair with priority ${issue.priority}." +
                    " A description of the issue: ${issue.description}"

            newIssue?.let {
                if (!dao.updateIssueCount(issue.equipmentId)) {
                    dao.addNewIssueCount(issue.equipmentId)
                }
                sendEmail(staffEmail, subject, message)
                call.respondText("${it.id}", status = HttpStatusCode.Created)
            } ?: call.respond(HttpStatusCode.InternalServerError)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid input")
        }
    }
}
fun Route.editIssueRoute() {
    authenticate("auth-session") {
        post("/issue/{id?}") {
            val id = call.parameters["id"] ?: return@post call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest,
            )
            try {
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
                            val htmlBody = buildSupportTicketEmailHtml(equipmentName ?: "", issue.description ?: "")
                            sendEmail(issue.assignedTo, subject, htmlBody)
                        }
                        call.respondText("Issue edited correctly", status = HttpStatusCode.Accepted)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError)
                    }
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid input")
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
