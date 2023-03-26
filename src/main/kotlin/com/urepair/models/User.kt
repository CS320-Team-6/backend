package com.urepair.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val role: String,
)

object UserTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val role = varchar("role", 255)

    override val primaryKey = PrimaryKey(IssueTable.id)
}
