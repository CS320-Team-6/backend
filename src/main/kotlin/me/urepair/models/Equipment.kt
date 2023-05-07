package me.urepair.models

import kotlinx.datetime.LocalDate
import kotlinx.datetime.serializers.LocalDateComponentSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

@Serializable
data class Equipment(
    val id: Int? = null, // Nullable
    val name: String,
    val equipmentType: String,
    val manufacturer: String,
    val model: String,
    val serialNumber: String,
    val location: String,
    @Serializable(with = LocalDateComponentSerializer::class)
    val dateInstalled: LocalDate,
    @Serializable(with = LocalDateComponentSerializer::class)
    val lastMaintenanceDate: LocalDate? = null,
)

object EquipmentTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val equipmentType = varchar("equipment_type", 255)
    val manufacturer = varchar("manufacturer", 255)
    val model = varchar("model", 255)
    val serialNumber = varchar("serial_number", 255)
    val location = varchar("location", 255)
    val dateInstalled = date("date_installed")
    val lastMaintenanceDate = date("last_maintenance_date").nullable()

    override val primaryKey = PrimaryKey(id)
}
