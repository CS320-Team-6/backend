package com.urepair.dao

import com.urepair.models.Equipment
import com.urepair.models.Issue
import com.urepair.models.IssueCount
import com.urepair.models.User
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface DAOFacade {
    suspend fun allEquipment(): List<Equipment>
    suspend fun equipment(id: Int): Equipment?
    suspend fun addNewEquipment(
        name: String,
        equipmentType: String,
        manufacturer: String,
        model: String,
        serialNumber: String,
        location: String,
        dateInstalled: LocalDate,
        lastMaintenanceDate: LocalDate?,
    ): Equipment?
    suspend fun editEquipment(
        id: Int,
        name: String,
        equipmentType: String,
        manufacturer: String,
        model: String,
        serialNumber: String,
        location: String,
        dateInstalled: LocalDate,
        lastMaintenanceDate: LocalDate?,
    ): Boolean
    suspend fun deleteEquipment(id: Int): Boolean

    suspend fun allIssues(): List<Issue>
    suspend fun issue(id: Int): Issue?
    suspend fun addNewIssue(
        equipmentId: Int,
        status: Issue.Status,
        dateReported: LocalDateTime,
        priority: Issue.Priority,
        description: String?,
        assignedTo: String?,
        dateResolved: LocalDateTime?,
        resolutionDetails: String?,
        notes: String?,
    ): Issue?
    suspend fun editIssue(
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
    ): Boolean
    suspend fun deleteIssue(id: Int): Boolean

    suspend fun allUsers(): List<User>
    suspend fun user(email: String): User?
    suspend fun addNewUser(
        firstName: String,
        lastName: String,
        email: String,
        role: User.Role,
    ): User?
    suspend fun editUser(
        firstName: String,
        lastName: String,
        email: String,
        role: User.Role,
    ): Boolean
    suspend fun deleteUser(email: String): Boolean
    suspend fun allIssueCounts(): List<IssueCount>
    suspend fun issueCount(equipmentId: Int): IssueCount?
    suspend fun deleteIssueCount(equipmentId: Int): Boolean
    suspend fun addNewIssueCount(equipmentId: Int): IssueCount?
    suspend fun updateIssueCount(equipmentId: Int, increment: Boolean = true): Boolean
}
