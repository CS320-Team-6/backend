package com.urepair

import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals

class EquipmentRouteTests {
    @Test
    fun testEquipment() = testApplication {
        // val authenticationProperties = loadAuthenticationProperties()
        val username = "team6"
        val password = "cs320Team6"
        val client = createClient {
            defaultRequest {
                val credentials = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
                header(HttpHeaders.Authorization, "Basic $credentials")
            }
        }

        val response = client.get("/equipment/1")
        assertEquals(
            """{
    "id": 1,
    "name": "name",
    "equipmentType": "type",
    "manufacturer": "man",
    "model": "model",
    "serialNumber": "serial",
    "location": "loc",
    "dateInstalled": {
        "year": 2023,
        "month": 3,
        "day": 19
    },
    "lastMaintenanceDate": {
        "year": 2023,
        "month": 3,
        "day": 20
    }
}""",
            response.bodyAsText(),
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
