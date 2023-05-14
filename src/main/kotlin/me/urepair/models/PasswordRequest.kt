package me.urepair.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.LocalDateTimeComponentSerializer
import kotlinx.serialization.Serializable
import me.urepair.utilities.isValidEmail
import me.urepair.utilities.sanitize
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

@Serializable
data class PasswordRequest(
    val email: String,
    val token: String,
    @Serializable(with = LocalDateTimeComponentSerializer::class)
    val expiresAt: LocalDateTime,
) { init {
    email.let {
        require(it.length <= 255) { "Email cannot exceed 255 characters" }
        require(isValidEmail(it)) { "Invalid email address" }
    }
    token.let {
        require(it.length <= 255) { "Token details cannot exceed 255 characters" }
        sanitize(it)
    }
} }

object PasswordRequestTable : Table() {
    val email = varchar("email", 255)
    val token = varchar("token", 255)
    val expiresAt = datetime("expiresAt")

    override val primaryKey = PrimaryKey(token)
}
