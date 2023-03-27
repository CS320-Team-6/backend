package com.urepair

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import kotlin.test.*

class OrderRouteTests {
    @Test
    fun testEquipment() = testApplication {
        //val authenticationProperties = loadAuthenticationProperties()
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
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
