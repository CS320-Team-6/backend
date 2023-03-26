package com.urepair.dao

import com.urepair.models.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*

object DatabaseFactory {
    fun init() {
        // val rdsEndpoint = "your-rds-endpoint"
        // val rdsPort = "your-rds-port"
        // val dbName = "urepair-db"
        // val username = "your-rds-username"
        // val password = "your-rds-password"

        // val driverClassName = "org.postgresql.Driver"
        // val jdbcUrl = "jdbc:postgresql://$rdsEndpoint:$rdsPort/$dbName"
        // val database = Database.connect(jdbcUrl, driverClassName, user = username, password = password)
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            SchemaUtils.create(EquipmentTable)
            SchemaUtils.create(IssueTable)
            SchemaUtils.create(UserTable)
        }
    }
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
