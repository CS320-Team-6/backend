package com.urepair.dao

import com.urepair.models.EquipmentTable
import com.urepair.models.IssueTable
import com.urepair.models.UserTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.FileInputStream
import java.util.Properties

object DatabaseFactory {
    private fun loadProperties(): Properties {
        val properties = Properties()
        val propertiesFile = FileInputStream("dbconfig.properties")
        properties.load(propertiesFile)
        propertiesFile.close()
        return properties
    }
    fun init() {
        // uncomment this section and edit dbconfig.properties to switch from local to amazon rds
        val properties = loadProperties()
        val rdsEndpoint = properties.getProperty("rdsEndpoint")
        val rdsPort = properties.getProperty("rdsPort")
        val username = properties.getProperty("username")
        val password = properties.getProperty("password")
        val driverClassName = properties.getProperty("driverClassName")
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
