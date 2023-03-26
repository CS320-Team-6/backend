package com.urepair.models

import com.urepair.models.IssueTable.references
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime
import kotlinx.datetime.serializers.LocalDateTimeComponentSerializer


@Serializable
data class Issue(
    val id: Int? = null, // Nullable
    val equipmentId: Int,
    val description: String?,
    val status: String,
    @Serializable(with = LocalDateTimeComponentSerializer::class)
    val dateReported: LocalDateTime,
    val priority: Int,
    val assignedTo: String?,
    @Serializable(with = LocalDateTimeComponentSerializer::class)
    val dateResolved: LocalDateTime?,
    val resolutionDetails: String?,
    val notes: String?
)

object IssueTable : Table() {
    val id = integer("id").autoIncrement()
    val equipmentId = integer("equipment_id") references EquipmentTable.id
    val description = varchar("description", 255).nullable()
    val status = varchar("status", 255)
    val dateReported = datetime("date_reported")
    val priority = integer("priority")
    val assignedTo = varchar("assignedTo", 255).references(UserTable.email).nullable()
    val dateResolved = datetime("date_resolved").nullable()
    val resolutionDetails = varchar("resolutionDetails", 255).nullable()
    val notes = varchar("notes", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}
