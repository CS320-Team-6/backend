package com.urepair.dao


import com.urepair.models.*
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
        status: String,
        dateReported: LocalDateTime,
        priority: Int,
        description: String?,
        assignedTo: String?,
        dateResolved: LocalDateTime?,
        resolutionDetails: String?,
        notes: String?
    ): Issue?
    suspend fun editIssue(
        id: Int,
        equipmentId: Int,
        status: String,
        dateReported: LocalDateTime,
        priority: Int,
        description: String?,
        assignedTo: String?,
        dateResolved: LocalDateTime?,
        resolutionDetails: String?,
        notes: String?
    ): Boolean
    suspend fun deleteIssue(id: Int): Boolean

    suspend fun allUsers(): List<User>
    suspend fun user(id: Int): User?
    suspend fun addNewUser(
        name: String,
        email: String,
        role: String
    ): User?
    suspend fun editUser(
        id: Int,
        name: String,
        email: String,
        role: String,
    ): Boolean
    suspend fun deleteUser(id: Int): Boolean
}
