package com.urepair.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.LocalDateTimeComponentSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

@Serializable
data class IssueCount(
    val equipmentId: Int,
    val issueCount: Int,
)

@Serializable
data class Issue(
    val id: Int? = null, // Nullable
    val equipmentId: Int,
    val status: Status,
    @Serializable(with = LocalDateTimeComponentSerializer::class)
    val dateReported: LocalDateTime,
    val priority: Priority,
    val description: String?,
    val assignedTo: String?,
    @Serializable(with = LocalDateTimeComponentSerializer::class)
    val dateResolved: LocalDateTime?,
    val resolutionDetails: String?,
    val notes: String?,
) { enum class Priority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT,
}
    enum class Status {
        NEW,
        IN_PROGRESS,
        RESOLVED,
        CLOSED,
    }
    init {
        if (assignedTo != null) {
            require(assignedTo.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex())) { "Invalid email address" }
        }
    }
}
object IssueCountTable : Table() {
    val equipmentId = integer("equipment_id") references EquipmentTable.id
    val issueCount = integer("issue_count")

    override val primaryKey = PrimaryKey(equipmentId)
}
object IssueTable : Table() {
    val id = integer("id").autoIncrement()
    val equipmentId = integer("equipment_id") references EquipmentTable.id
    val status = enumerationByName("status", 255, Issue.Status::class)
    val dateReported = datetime("date_reported")
    val priority = enumerationByName("priority", 255, Issue.Priority::class)
    val description = varchar("description", 255).nullable()
    val assignedTo = varchar("assignedTo", 255).references(UserTable.email).nullable()
    val dateResolved = datetime("date_resolved").nullable()
    val resolutionDetails = varchar("resolutionDetails", 255).nullable()
    val notes = varchar("notes", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}
