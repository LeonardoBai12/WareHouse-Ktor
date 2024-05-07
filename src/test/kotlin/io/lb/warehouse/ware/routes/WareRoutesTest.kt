package io.lb.warehouse.ware.routes

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.lb.warehouse.util.setupApplication
import io.lb.warehouse.util.setupRequest
import io.lb.warehouse.ware.data.model.WareData
import io.lb.warehouse.ware.data.service.WareDatabaseService
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class WareRoutesTest {
    private val service: WareDatabaseService = mockk()

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Creating ware with typos, should return BadRequest`() = testApplication {
        setup()

        val response = client.post("/api/createWare") {
            setupRequest()
            setBody(
                """
                {
                    "userId": "75ba8951-d1cd-46cb-bde7-39caa35a8929",
                    "nameTypo": "Motor de partida Valtra BT",
                    "branD": "Valtra",
                    "descriptionTypo": "Novo",
                    "weightPerUnit": 8.0,
                    "weightUnitTypo": "kg",
                    "availableQuantity": 500.0,
                    "quantityUnitTypo": "pc",
                    "wareLocation": "Gaveta 1 Prateleira 23"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Creating ware correctly, should return Created`() = testApplication {
        setup()
        coEvery { service.insertWare(any()) } returns ""

        val response = client.post("/api/createWare") {
            setupRequest()
            setBody(
                """
                {
                    "userId": "75ba8951-d1cd-46cb-bde7-39caa35a8929",
                    "name": "Motor de partida Valtra BT",
                    "brand": "Valtra",
                    "description": "Novo",
                    "weightPerUnit": 8.0,
                    "weightUnit": "kg",
                    "availableQuantity": 500.0,
                    "quantityUnit": "pc",
                    "wareLocation": "Gaveta 1 Prateleira 23"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Created)
    }

    @Test
    fun `Updating unexistent ware, should return NotFound`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getWareById(uuid) } returns null

        val response = client.put("/api/updateWare") {
            setupRequest()
            parameter("id", uuid)
            setBody(
                """
                {
                    "userId": "75ba8951-d1cd-46cb-bde7-39caa35a8929",
                    "name": "Novo Motor de partida Valtra BT",
                    "brand": "Valtra",
                    "description": "Novo",
                    "weightPerUnit": 8.0,
                    "weightUnit": "kg",
                    "availableQuantity": 500.0,
                    "quantityUnit": "pc",
                    "wareLocation": "Gaveta 1 Prateleira 23"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There is no wares with such ID")
    }

    @Test
    fun `Updating without id param, should return BadRequest`() = testApplication {
        setup()

        val response = client.put("/api/updateWare") {
            setupRequest()
            setBody(
                """
                {
                    "userId": "75ba8951-d1cd-46cb-bde7-39caa35a8929",
                    "name": "Novo Motor de partida Valtra BT",
                    "brand": "Valtra",
                    "description": "Novo",
                    "weightPerUnit": 8.0,
                    "weightUnit": "kg",
                    "availableQuantity": 500.0,
                    "quantityUnit": "pc",
                    "wareLocation": "Gaveta 1 Prateleira 23"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Updating ware correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        setup()

        coEvery { service.getWareById(uuid) } returns WareData(
            uuid = uuid,
            userId = userId,
            name = "Novo Motor de partida Valtra BT",
            brand = "Valtra",
            description = "Novo",
            weightPerUnit = 8.0,
            weightUnit = "kg",
            availableQuantity = 500.0,
            quantityUnit = "pc",
            wareLocation = "Gaveta 1 Prateleira 23",
            timestamp = "2024-05-04 16:37:33.870626-03"
        )
        coEvery { service.updateWare(any(), any()) } returns 1

        val response = client.put("/api/updateWare") {
            setupRequest()
            parameter("id", uuid)
            setBody(
                """
                {
                    "userId": "$userId",
                    "name": "Novo Motor de partida Valtra BT",
                    "brand": "Valtra",
                    "description": "Novo",
                    "weightPerUnit": 8.0,
                    "weightUnit": "kg",
                    "availableQuantity": 500.0,
                    "quantityUnit": "pc",
                    "wareLocation": "Gaveta 1 Prateleira 23"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Deleting with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.delete("/api/deleteWare") {
            setupRequest()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Deleting unexistent ware, should return NotFound`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getWareById(uuid) } returns null

        val response = client.delete("/api/deleteWare") {
            setupRequest()
            parameter("id", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There is no wares with such ID")
    }

    @Test
    fun `Deleting ware correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        setup()

        coEvery { service.getWareById(uuid) } returns WareData(
            uuid = uuid,
            userId = userId,
            name = "Novo Motor de partida Valtra BT",
            brand = "Valtra",
            description = "Novo",
            weightPerUnit = 8.0,
            weightUnit = "kg",
            availableQuantity = 500.0,
            quantityUnit = "pc",
            wareLocation = "Gaveta 1 Prateleira 23",
            timestamp = "2024-05-04 16:37:33.870626-03"
        )
        coEvery { service.deleteWare(any()) } returns 1

        val response = client.delete("/api/deleteWare") {
            setupRequest()
            parameter("id", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        assertThat(response.bodyAsText()).isEqualTo("Ware deleted successfully")
    }

    @Test
    fun `Getting with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.get("/api/ware") {
            setupRequest()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Getting unexistent ware, should return NotFound`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getWareById(uuid) } returns null

        val response = client.get("/api/ware") {
            setupRequest()
            parameter("id", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There is no wares with such ID")
    }

    @Test
    fun `Getting ware correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        setup()

        coEvery { service.getWareById(uuid) } returns WareData(
            uuid = uuid,
            userId = userId,
            name = "Novo Motor de partida Valtra BT",
            brand = "Valtra",
            description = "Novo",
            weightPerUnit = 8.0,
            weightUnit = "kg",
            availableQuantity = 500.0,
            quantityUnit = "pc",
            wareLocation = "Gaveta 1 Prateleira 23",
            timestamp = "2024-05-04 16:37:33.870626-03"
        )

        val response = client.get("/api/ware") {
            setupRequest()
            parameter("id", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting by userId with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Getting by unexistent userId, should return NotFound`() = testApplication {
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        setup()

        coEvery { service.getWaresByUserId(userId) } returns listOf()

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There is no wares for such user")
    }

    @Test
    fun `Getting ware by userId correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        setup()

        coEvery { service.getWaresByUserId(userId) } returns listOf(
            WareData(
                uuid = uuid,
                userId = userId,
                name = "Novo Motor de partida Valtra BT",
                brand = "Valtra",
                description = "Novo",
                weightPerUnit = 8.0,
                weightUnit = "kg",
                availableQuantity = 500.0,
                quantityUnit = "pc",
                wareLocation = "Gaveta 1 Prateleira 23",
                timestamp = "2024-05-04 16:37:33.870626-03"
            )
        )

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    private fun ApplicationTestBuilder.setup() {
        setupApplication {
            wareRoutes(service)
        }
    }
}
