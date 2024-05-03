package io.lb.warehouse.deposit.routes

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.lb.warehouse.deposit.data.service.DepositDatabaseService
import io.lb.warehouse.deposit.data.model.DepositData
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class DepositRoutesTest {
    private val service: DepositDatabaseService = mockk()

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Creating deposit with typos, should return BadRequest`() = testApplication {
        setup()

        val response = client.post("/api/createDeposit") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """
                {
                    "userTypoId": "75ba8951-d1cd-46cb-bde7-39caa35a8929",
                    "wareId": "d5745279-6bbe-4d73-95ae-ba43dbd46b47",
                    "quantity": 10.0
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Creating deposit correctly, should return Created`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.insertDeposit(any()) } returns 1

        val response = client.post("/api/createDeposit") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """
                {
                    "userId": "75ba8951-d1cd-46cb-bde7-39caa35a8929",
                    "wareId": "d5745279-6bbe-4d73-95ae-ba43dbd46b47",
                    "quantity": 10.0
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Created)
    }

    @Test
    fun `Getting with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.get("/api/deposit") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Getting unexistent deposit, should return NotFound`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getDepositById(uuid) } returns null

        val response = client.get("/api/deposit") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("id", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }

    @Test
    fun `Getting deposit correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val wareId = "390fddcd-d238-442a-9c44-8c1a3c29cd4e"

        setup()

        coEvery { service.getDepositById(uuid) } returns DepositData(
            uuid = uuid,
            quantity = 500.0,
            userId=  userId,
            wareId = wareId,
        )

        val response = client.get("/api/deposit") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("id", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting by userId with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.get("/api/depositsCreatedByUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Getting by unexistent userId, should return NotFound`() = testApplication {
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        setup()

        coEvery { service.getDepositsByUserId(userId) } returns listOf()

        val response = client.get("/api/depositsCreatedByUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", userId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }

    @Test
    fun `Getting deposit by userId correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val wareId = "390fddcd-d238-442a-9c44-8c1a3c29cd4e"

        setup()

        coEvery { service.getDepositsByUserId(userId) } returns listOf(
            DepositData(
                uuid = uuid,
                quantity = 500.0,
                userId=  userId,
                wareId = wareId,
            )
        )

        val response = client.get("/api/depositsCreatedByUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", userId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting by wareId with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.get("/api/depositsByWareId") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Getting by unexistent wareId, should return NotFound`() = testApplication {
        val wareId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        setup()

        coEvery { service.getDepositsByWareId(wareId) } returns listOf()

        val response = client.get("/api/depositsByWareId") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("wareId", wareId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }

    @Test
    fun `Getting deposit by wareId correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val wareId = "390fddcd-d238-442a-9c44-8c1a3c29cd4e"

        setup()

        coEvery { service.getDepositsByWareId(wareId) } returns listOf(
            DepositData(
                uuid = uuid,
                quantity = 500.0,
                userId=  userId,
                wareId = wareId,
            )
        )

        val response = client.get("/api/depositsByWareId") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("wareId", wareId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    private fun ApplicationTestBuilder.setup() {
        install(ContentNegotiation) {
            json()
            gson {
            }
        }
        application {
            depositRoutes(service)
        }
    }
}
