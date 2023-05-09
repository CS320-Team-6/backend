package me.urepair.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.LocalDateTimeComponentSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

@Serializable
data class PasswordRequest(
    val email: String,
    val token: String,
    @Serializable(with = LocalDateTimeComponentSerializer::class)
    val expiresAt: LocalDateTime,
)

object PasswordRequestTable : Table() {
    val email = varchar("email", 255)
    val token = varchar("token", 255)
    val expiresAt = datetime("expiresAt")

    override val primaryKey = PrimaryKey(email)
}
