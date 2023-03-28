package com.urepair.dao

import com.urepair.dao.DatabaseFactory.dbQuery
import com.urepair.models.Equipment
import com.urepair.models.EquipmentTable
import com.urepair.models.Issue
import com.urepair.models.IssueTable
import com.urepair.models.User
import com.urepair.models.UserTable
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToEquipment(row: ResultRow) = Equipment(
        id = row[EquipmentTable.id],
        name = row[EquipmentTable.name],
        equipmentType = row[EquipmentTable.equipmentType],
        manufacturer = row[EquipmentTable.manufacturer],
        model = row[EquipmentTable.model],
        serialNumber = row[EquipmentTable.serialNumber],
        location = row[EquipmentTable.location],
        dateInstalled = row[EquipmentTable.dateInstalled].toKotlinLocalDate(),
        lastMaintenanceDate = row[EquipmentTable.lastMaintenanceDate]?.toKotlinLocalDate(),
    )
    private fun resultRowToIssue(row: ResultRow) = Issue(
        id = row[IssueTable.id],
        equipmentId = row[IssueTable.equipmentId],
        description = row[IssueTable.description],
        status = row[IssueTable.status],
        dateReported = row[IssueTable.dateReported].toKotlinLocalDateTime(),
        priority = row[IssueTable.priority],
        assignedTo = row[IssueTable.assignedTo],
        dateResolved = row[IssueTable.dateResolved]?.toKotlinLocalDateTime(),
        resolutionDetails = row[IssueTable.resolutionDetails],
        notes = row[IssueTable.notes],
    )

    private fun resultRowToUser(row: ResultRow) = User(
        firstName = row[UserTable.firstName],
        lastName = row[UserTable.lastName],
        email = row[UserTable.email],
        role = row[UserTable.role],
    )
    private fun setEquipmentValues(
        it: UpdateBuilder<*>,
        name: String,
        equipmentType: String,
        manufacturer: String,
        model: String,
        serialNumber: String,
        location: String,
        dateInstalled: LocalDate,
        lastMaintenanceDate: LocalDate? = null,
    ) {
        it[EquipmentTable.name] = name
        it[EquipmentTable.equipmentType] = equipmentType
        it[EquipmentTable.manufacturer] = manufacturer
        it[EquipmentTable.model] = model
        it[EquipmentTable.serialNumber] = serialNumber
        it[EquipmentTable.location] = location
        it[EquipmentTable.dateInstalled] = dateInstalled.toJavaLocalDate()
        it[EquipmentTable.lastMaintenanceDate] = lastMaintenanceDate?.toJavaLocalDate()
    }

    private fun setIssueValues(
        it: UpdateBuilder<*>,
        equipmentId: Int,
        status: Issue.Status,
        dateReported: LocalDateTime,
        priority: Issue.Priority,
        description: String? = null,
        assignedTo: String? = null,
        dateResolved: LocalDateTime? = null,
        resolutionDetails: String? = null,
        notes: String? = null,
    ) {
        it[IssueTable.equipmentId] = equipmentId
        it[IssueTable.status] = status
        it[IssueTable.dateReported] = dateReported.toJavaLocalDateTime()
        it[IssueTable.priority] = priority
        it[IssueTable.description] = description
        it[IssueTable.assignedTo] = assignedTo
        it[IssueTable.dateResolved] = dateResolved?.toJavaLocalDateTime()
        it[IssueTable.resolutionDetails] = resolutionDetails
        it[IssueTable.notes] = notes
    }
    private fun setUserValues(
        it: UpdateBuilder<*>,
        firstName: String,
        lastName: String,
        email: String,
        role: User.Role,
    ) {
        it[UserTable.firstName] = firstName
        it[UserTable.lastName] = lastName
        it[UserTable.email] = email
        it[UserTable.role] = role
    }
    override suspend fun allEquipment(): List<Equipment> = dbQuery {
        EquipmentTable.selectAll().map(::resultRowToEquipment)
    }

    override suspend fun equipment(id: Int): Equipment? = dbQuery {
        EquipmentTable
            .select { EquipmentTable.id eq id }
            .map(::resultRowToEquipment)
            .singleOrNull()
    }

    override suspend fun addNewEquipment(
        name: String,
        equipmentType: String,
        manufacturer: String,
        model: String,
        serialNumber: String,
        location: String,
        dateInstalled: LocalDate,
        lastMaintenanceDate: LocalDate?,
    ): Equipment? = dbQuery {
        val insertStatement = EquipmentTable.insert {
            setEquipmentValues(it, name, equipmentType, manufacturer, model, serialNumber, location, dateInstalled, lastMaintenanceDate)
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToEquipment)
    }

    override suspend fun editEquipment(
        id: Int,
        name: String,
        equipmentType: String,
        manufacturer: String,
        model: String,
        serialNumber: String,
        location: String,
        dateInstalled: LocalDate,
        lastMaintenanceDate: LocalDate?,
    ): Boolean = dbQuery {
        EquipmentTable.update({ EquipmentTable.id eq id }) {
            setEquipmentValues(it, name, equipmentType, manufacturer, model, serialNumber, location, dateInstalled, lastMaintenanceDate)
        } > 0
    }

    override suspend fun deleteEquipment(id: Int): Boolean = dbQuery {
        EquipmentTable.deleteWhere { EquipmentTable.id eq id } > 0
    }

    override suspend fun allIssues(): List<Issue> = dbQuery {
        IssueTable.selectAll().map(::resultRowToIssue)
    }

    override suspend fun issue(id: Int): Issue? = dbQuery {
        IssueTable
            .select { IssueTable.id eq id }
            .map(::resultRowToIssue)
            .singleOrNull()
    }

    override suspend fun addNewIssue(
        equipmentId: Int,
        status: Issue.Status,
        dateReported: LocalDateTime,
        priority: Issue.Priority,
        description: String?,
        assignedTo: String?,
        dateResolved: LocalDateTime?,
        resolutionDetails: String?,
        notes: String?,
    ): Issue? = dbQuery {
        val insertStatement = IssueTable.insert {
            setIssueValues(it, equipmentId, status, dateReported, priority, description, assignedTo, dateResolved, resolutionDetails, notes)
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToIssue)
    }

    override suspend fun editIssue(
        id: Int,
        equipmentId: Int,
        status: Issue.Status,
        dateReported: LocalDateTime,
        priority: Issue.Priority,
        description: String?,
        assignedTo: String?,
        dateResolved: LocalDateTime?,
        resolutionDetails: String?,
        notes: String?,
    ): Boolean = dbQuery {
        IssueTable.update({ IssueTable.id eq id }) {
            setIssueValues(it, equipmentId, status, dateReported, priority, description, assignedTo, dateResolved, resolutionDetails, notes)
        } > 0
    }

    override suspend fun deleteIssue(id: Int): Boolean = dbQuery {
        IssueTable.deleteWhere { IssueTable.id eq id } > 0
    }

    override suspend fun allUsers(): List<User> = dbQuery {
        UserTable.selectAll().map(::resultRowToUser)
    }

    override suspend fun user(email: String): User? = dbQuery {
        UserTable
            .select { UserTable.email eq email }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun addNewUser(firstName: String, lastName: String, email: String, role: User.Role): User? = dbQuery {
        val insertStatement = UserTable.insert {
            setUserValues(it, firstName, lastName, email, role)
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun editUser(firstName: String, lastName: String, email: String, role: User.Role): Boolean = dbQuery {
        UserTable.update({ UserTable.email eq email }) {
            setUserValues(it, firstName, lastName, email, role)
        } > 0
    }

    override suspend fun deleteUser(email: String): Boolean = dbQuery {
        UserTable.deleteWhere { UserTable.email eq email } > 0
    }
}

val dao: DAOFacade = DAOFacadeImpl().apply {
    runBlocking {
        if (allEquipment().isEmpty()) {
            addNewEquipment("name", "type", "man", "model", "serial", "loc", LocalDate(2023, 3, 19), LocalDate(2023, 3, 20))
        }
        if (allUsers().isEmpty()) {
            addNewUser("john", "wordell", "jwordell@umass.edu", User.Role.valueOf("STUDENT"))
        }
        if (allIssues().isEmpty()) {
            addNewIssue(1, Issue.Status.valueOf("NEW"), LocalDateTime(2023, 3, 5, 2, 15), Issue.Priority.valueOf("LOW"), null, "jwordell@umass.edu", null, null, null)
        }
    }
}
