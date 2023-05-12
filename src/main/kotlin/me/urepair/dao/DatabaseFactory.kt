package me.urepair.dao

import kotlinx.coroutines.Dispatchers
import me.urepair.models.EquipmentTable
import me.urepair.models.IssueCountTable
import me.urepair.models.IssueTable
import me.urepair.models.PasswordRequestTable
import me.urepair.models.UserTable
import me.urepair.secrets.getRdsSecret
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val awsSecret = getRdsSecret("urepair/rds")
        val rdsEndpoint = awsSecret.rdsEndpoint
        val rdsPort = awsSecret.rdsPort
        val username = awsSecret.rdsUsername
        val password = awsSecret.rdsSecret
        val driverClassName = "org.postgresql.Driver"
        val jdbcUrl = "jdbc:postgresql://$rdsEndpoint:$rdsPort/?ssl=true&sslmode=require"
        val database = Database.connect(jdbcUrl, driverClassName, user = username, password = password)

        transaction(database) {
            SchemaUtils.create(EquipmentTable)
            SchemaUtils.create(IssueTable)
            SchemaUtils.create(UserTable)
            SchemaUtils.create(IssueCountTable)
            SchemaUtils.create(PasswordRequestTable)
        }
    }
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
