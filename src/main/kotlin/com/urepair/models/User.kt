package com.urepair.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: Role,
) { enum class Role {
    STAFF,
    STUDENT,
}
    init {
        require(email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex())) { "Invalid email address" }
    }
}

object UserTable : Table() {
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val email = varchar("email", 255)
    val role = enumerationByName("role", 255, User.Role::class)

    override val primaryKey = PrimaryKey(email)
}
