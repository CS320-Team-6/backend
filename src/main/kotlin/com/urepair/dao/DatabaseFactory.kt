package com.urepair.dao

import com.urepair.models.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*

object DatabaseFactory {
    fun init() {
        // uncomment this section and edit dbconfig.properties to switch from local to amazon rds
        // val rdsEndpoint = "rdsEndpoint"
        // val rdsPort = "rdsPort"
        // val dbName = "dbName"
        // val username = "username"
        // val password = "password"
        // val driverClassName = "org.postgresql.Driver"
        // val jdbcUrl = "jdbc:postgresql://$rdsEndpoint:$rdsPort/$dbName"
        // val database = Database.connect(jdbcUrl, driverClassName, user = username, password = password)

        // comment this block when using rds
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
