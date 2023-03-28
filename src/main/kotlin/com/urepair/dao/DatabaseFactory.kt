package com.urepair.dao

import com.urepair.models.EquipmentTable
import com.urepair.models.IssueTable
import com.urepair.models.UserTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        // uncomment this section and edit dbconfig.properties to switch from local to amazon rds
        val rdsEndpoint = "database-1.cd9hpkls9dip.us-east-2.rds.amazonaws.com"
        val rdsPort = 5432
        val username = "postgres"
        val password = "eTzE2QnrRg4NdnKy9l9O"
        val driverClassName = "org.postgresql.Driver"
        val jdbcUrl = "jdbc:postgresql://$rdsEndpoint:$rdsPort/"
        val database = Database.connect(jdbcUrl, driverClassName, user = username, password = password)

        transaction(database) {
            SchemaUtils.create(EquipmentTable)
            SchemaUtils.create(IssueTable)
            SchemaUtils.create(UserTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =

        newSuspendedTransaction(Dispatchers.IO) { block() }
}
