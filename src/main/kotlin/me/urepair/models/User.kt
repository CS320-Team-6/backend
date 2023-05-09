package me.urepair.models

import kotlinx.serialization.Serializable
import me.urepair.utilities.isValidEmail
import me.urepair.utilities.sanitize
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
} init {
    firstName.let {
        require(it.length <= 255) { "First name cannot exceed 255 characters" }
        sanitize(it)
    }
    lastName.let {
        require(it.length <= 255) { "Last name cannot exceed 255 characters" }
        sanitize(it)
    }
    email.let {
        require(it.length <= 255) { "Email cannot exceed 255 characters" }
        require(isValidEmail(it)) { "Invalid email address" }
    }
} }

@Serializable
data class Email(val email: String) { init {
    email.let {
        require(it.length <= 255) { "Email cannot exceed 255 characters" }
        require(isValidEmail(it)) { "Invalid email address" }
    }
} }

@Serializable
data class ResetPassword(val token: String, val newPassword: String)

object UserTable : Table() {
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val email = varchar("email", 255)
    val role = enumerationByName("role", 255, User.Role::class)

    override val primaryKey = PrimaryKey(email)
}
