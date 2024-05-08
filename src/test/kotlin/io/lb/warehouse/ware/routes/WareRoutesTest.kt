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
import io.ktor.server.application.install
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.lb.warehouse.util.setupApplication
import io.lb.warehouse.util.setupRequest
import io.lb.warehouse.ware.data.model.WareData
import io.lb.warehouse.ware.data.repository.WareRepositoryImpl
import io.lb.warehouse.ware.data.service.WareDatabaseService
import io.lb.warehouse.ware.data.service.WareDatabaseServiceImpl
import io.lb.warehouse.ware.domain.repository.WareRepository
import io.lb.warehouse.ware.domain.use_cases.CreateWareUseCase
import io.lb.warehouse.ware.domain.use_cases.DeleteWareUseCase
import io.lb.warehouse.ware.domain.use_cases.GetWareByIdUseCase
import io.lb.warehouse.ware.domain.use_cases.GetWaresByUserIdUseCase
import io.lb.warehouse.ware.domain.use_cases.UpdateWareUseCase
import io.lb.warehouse.ware.domain.use_cases.WareUseCases
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

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

        val ware1 = wareData1(uuid, userId)
        val ware2 = wareData2(uuid, userId)
        val ware3 = wareData3(uuid, userId)

        setup()

        coEvery { service.getWaresByUserId(userId) } returns listOf(
            ware1,
            ware2,
            ware3,
        )

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
        }

        assertThat(
            Json.decodeFromString<List<WareData>>(response.bodyAsText())
        ).isEqualTo(
            listOf(ware1, ware3, ware2)
        )
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting ware by userId with unexistent order, should return BadRequest`() = testApplication {
        val uuid = "d5745277-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8911-d1cd-46cb-bde7-39caa35a8929"

        val ware1 = wareData1(uuid, userId)
        val ware2 = wareData2(uuid, userId)
        val ware3 = wareData3(uuid, userId)

        setup()

        coEvery { service.getWaresByUserId(userId) } returns listOf(
            ware1,
            ware2,
            ware3,
        )

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
            parameter("order", "unexistent")
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
        assertThat(response.bodyAsText()).isEqualTo("Order should be: [asc, desc]")
    }

    @Test
    fun `Getting ware by userId with unexistent sorting type, should return BadRequest`() = testApplication {
        val uuid = "d5745277-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8911-d1cd-46cb-bde7-39caa35a8929"

        val ware1 = wareData1(uuid, userId)
        val ware2 = wareData2(uuid, userId)
        val ware3 = wareData3(uuid, userId)

        setup()

        coEvery { service.getWaresByUserId(userId) } returns listOf(
            ware1,
            ware2,
            ware3,
        )

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
            parameter("sortBy", "unexistent")
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
        assertThat(response.bodyAsText()).isEqualTo("Sorting should be: [name, brand, quantity, timestamp]")
    }

    @Test
    fun `Getting ware by userId sorting by name descending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        val ware1 = wareData1(uuid, userId)
        val ware2 = wareData2(uuid, userId)
        val ware3 = wareData3(uuid, userId)

        setup()

        coEvery { service.getWaresByUserId(userId) } returns listOf(
            ware1,
            ware2,
            ware3,
        )

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
            parameter("sortBy", "name")
            parameter("order", "desc")
        }

        val result = Json.decodeFromString<List<WareData>>(response.bodyAsText())
        assertThat(result[0].name).isEqualTo("Novo Motor de partida Valtra BT")
        assertThat(result[1].name).isEqualTo("Motor de partida T7")
        assertThat(result[2].name).isEqualTo("Exemplo")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting ware by userId sorting by name ascending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        val ware1 = wareData1(uuid, userId)
        val ware2 = wareData2(uuid, userId)
        val ware3 = wareData3(uuid, userId)

        setup()

        coEvery { service.getWaresByUserId(userId) } returns listOf(
            ware1,
            ware2,
            ware3,
        )

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
            parameter("sortBy", "name")
            parameter("order", "asc")
        }

        val result = Json.decodeFromString<List<WareData>>(response.bodyAsText())
        assertThat(result[0].name).isEqualTo("Exemplo")
        assertThat(result[1].name).isEqualTo("Motor de partida T7")
        assertThat(result[2].name).isEqualTo("Novo Motor de partida Valtra BT")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting ware by userId sorting by brand descending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        val ware1 = wareData1(uuid, userId)
        val ware2 = wareData2(uuid, userId)
        val ware3 = wareData3(uuid, userId)

        setup()

        coEvery { service.getWaresByUserId(userId) } returns listOf(
            ware1,
            ware2,
            ware3,
        )

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
            parameter("sortBy", "brand")
            parameter("order", "desc")
        }

        val result = Json.decodeFromString<List<WareData>>(response.bodyAsText())
        assertThat(result[0].brand).isEqualTo("Valtra")
        assertThat(result[1].brand).isEqualTo("New Holland")
        assertThat(result[2].brand).isEqualTo("Adidas")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting ware by userId sorting by brand ascending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        val ware1 = wareData1(uuid, userId)
        val ware2 = wareData2(uuid, userId)
        val ware3 = wareData3(uuid, userId)

        setup()

        coEvery { service.getWaresByUserId(userId) } returns listOf(
            ware1,
            ware2,
            ware3,
        )

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
            parameter("sortBy", "brand")
            parameter("order", "asc")
        }

        val result = Json.decodeFromString<List<WareData>>(response.bodyAsText())
        assertThat(result[0].brand).isEqualTo("Adidas")
        assertThat(result[1].brand).isEqualTo("New Holland")
        assertThat(result[2].brand).isEqualTo("Valtra")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting ware by userId sorting by quantity descending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        val ware1 = wareData1(uuid, userId)
        val ware2 = wareData2(uuid, userId)
        val ware3 = wareData3(uuid, userId)

        setup()

        coEvery { service.getWaresByUserId(userId) } returns listOf(
            ware1,
            ware2,
            ware3,
        )

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
            parameter("sortBy", "quantity")
            parameter("order", "desc")
        }

        val result = Json.decodeFromString<List<WareData>>(response.bodyAsText())
        assertThat(result[0].availableQuantity).isEqualTo(590.0)
        assertThat(result[1].availableQuantity).isEqualTo(500.0)
        assertThat(result[2].availableQuantity).isEqualTo(50.0)
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting ware by userId sorting by quantity ascending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        val ware1 = wareData1(uuid, userId)
        val ware2 = wareData2(uuid, userId)
        val ware3 = wareData3(uuid, userId)

        setup()

        coEvery { service.getWaresByUserId(userId) } returns listOf(
            ware1,
            ware2,
            ware3,
        )

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
            parameter("sortBy", "quantity")
            parameter("order", "asc")
        }

        val result = Json.decodeFromString<List<WareData>>(response.bodyAsText())
        assertThat(result[0].availableQuantity).isEqualTo(50.0)
        assertThat(result[1].availableQuantity).isEqualTo(500.0)
        assertThat(result[2].availableQuantity).isEqualTo(590.0)
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting ware by userId sorting by timestamp descending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        val ware1 = wareData1(uuid, userId)
        val ware2 = wareData2(uuid, userId)
        val ware3 = wareData3(uuid, userId)

        setup()

        coEvery { service.getWaresByUserId(userId) } returns listOf(
            ware1,
            ware2,
            ware3,
        )

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
            parameter("sortBy", "timestamp")
            parameter("order", "desc")
        }

        val result = Json.decodeFromString<List<WareData>>(response.bodyAsText())
        assertThat(result[0].timestamp).isEqualTo("2024-06-04 16:37:33.870626-03")
        assertThat(result[1].timestamp).isEqualTo("2024-05-05 16:37:33.870626-03")
        assertThat(result[2].timestamp).isEqualTo("2024-05-04 16:37:33.870626-03")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting ware by userId sorting by timestamp ascending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        val ware1 = wareData1(uuid, userId)
        val ware2 = wareData2(uuid, userId)
        val ware3 = wareData3(uuid, userId)

        setup()

        coEvery { service.getWaresByUserId(userId) } returns listOf(
            ware1,
            ware2,
            ware3,
        )

        val response = client.get("/api/waresCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
        }

        val result = Json.decodeFromString<List<WareData>>(response.bodyAsText())
        assertThat(result[0].timestamp).isEqualTo("2024-05-04 16:37:33.870626-03")
        assertThat(result[1].timestamp).isEqualTo("2024-05-05 16:37:33.870626-03")
        assertThat(result[2].timestamp).isEqualTo("2024-06-04 16:37:33.870626-03")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    private fun wareData3(uuid: String, userId: String) = WareData(
        uuid = uuid,
        userId = userId,
        name = "Exemplo",
        brand = "Adidas",
        description = "Novo",
        weightPerUnit = 8.0,
        weightUnit = "kg",
        availableQuantity = 50.0,
        quantityUnit = "pc",
        wareLocation = "Gaveta 1 Prateleira 23",
        timestamp = "2024-05-05 16:37:33.870626-03"
    )

    private fun wareData2(uuid: String, userId: String) = WareData(
        uuid = uuid,
        userId = userId,
        name = "Novo Motor de partida Valtra BT",
        brand = "Valtra",
        description = "Novo",
        weightPerUnit = 8.0,
        weightUnit = "kg",
        availableQuantity = 590.0,
        quantityUnit = "pc",
        wareLocation = "Gaveta 1 Prateleira 23",
        timestamp = "2024-06-04 16:37:33.870626-03"
    )

    private fun wareData1(uuid: String, userId: String) = WareData(
        uuid = uuid,
        userId = userId,
        name = "Motor de partida T7",
        brand = "New Holland",
        description = "Novo",
        weightPerUnit = 8.0,
        weightUnit = "kg",
        availableQuantity = 500.0,
        quantityUnit = "pc",
        wareLocation = "Gaveta 1 Prateleira 23",
        timestamp = "2024-05-04 16:37:33.870626-03"
    )

    private fun ApplicationTestBuilder.setup() {
        setupApplication {
            install(Koin) {
                slf4jLogger()
                modules(wareModule)
            }
            wareRoutes()
        }
    }

    private val wareModule = module {
        single<WareRepository> {
            WareRepositoryImpl(service)
        }
        single {
            WareUseCases(
                createWareUseCase = CreateWareUseCase(get()),
                deleteWareUseCase = DeleteWareUseCase(get()),
                getWareByIdUseCase = GetWareByIdUseCase(get()),
                getWaresByUserIdUseCase = GetWaresByUserIdUseCase(get()),
                updateWareUseCase = UpdateWareUseCase(get()),
            )
        }
    }
}
