package com.urepair.dao

import com.urepair.models.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*
// import java.io.FileInputStream
// import java.util.*

object DatabaseFactory {
//    private fun loadProperties(): Properties {
//        val properties = Properties()
//        val propertiesFile = FileInputStream("dbconfig.properties")
//        properties.load(propertiesFile)
//        propertiesFile.close()
//        return properties
//    }

    fun init() {
        // uncomment this section and edit dbconfig.properties to switch from local to amazon rds
        // val properties = loadProperties()
        // val rdsEndpoint = properties.getProperty("rdsEndpoint")
        // val rdsPort = properties.getProperty("rdsPort")
        // val dbName = properties.getProperty("dbName")
        // val username = properties.getProperty("username")
        // val password = properties.getProperty("password")
        // val driverClassName = properties.getProperty("driverClassName")
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
