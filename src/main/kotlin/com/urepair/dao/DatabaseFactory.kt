package com.urepair.dao

import com.urepair.models.EquipmentTable
import com.urepair.models.IssueCountTable
import com.urepair.models.IssueTable
import com.urepair.models.UserTable
import com.urepair.secrets.getRdsSecret
import kotlinx.coroutines.Dispatchers
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
        val jdbcUrl = "jdbc:postgresql://$rdsEndpoint:$rdsPort/"
        val database = Database.connect(jdbcUrl, driverClassName, user = username, password = password)

        transaction(database) {
            SchemaUtils.create(EquipmentTable)
            SchemaUtils.create(IssueTable)
            SchemaUtils.create(UserTable)
            SchemaUtils.create(IssueCountTable)
        }
    }
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
