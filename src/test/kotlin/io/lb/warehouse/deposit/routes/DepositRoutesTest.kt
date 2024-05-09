package io.lb.warehouse.deposit.routes

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.lb.warehouse.deposit.data.model.DepositData
import io.lb.warehouse.deposit.data.repository.DepositRepositoryImpl
import io.lb.warehouse.deposit.data.service.DepositDatabaseServiceImpl
import io.lb.warehouse.deposit.domain.repository.DepositRepository
import io.lb.warehouse.deposit.domain.use_cases.CreateDepositUseCase
import io.lb.warehouse.deposit.domain.use_cases.DepositUseCases
import io.lb.warehouse.deposit.domain.use_cases.GetDepositByIDUseCase
import io.lb.warehouse.deposit.domain.use_cases.GetDepositsUseCase
import io.lb.warehouse.util.setupApplication
import io.lb.warehouse.util.setupRequest
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

class DepositRoutesTest {
    private val service: DepositDatabaseServiceImpl = mockk()

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Creating deposit with typos, should return BadRequest`() = testApplication {
        setup()

        val response = client.post("/api/createDeposit") {
            setupRequest()
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
        setup()
        coEvery { service.insertDeposit(any()) } returns ""

        val response = client.post("/api/createDeposit") {
            setupRequest()
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
            setupRequest()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Getting unexistent deposit, should return NotFound`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getDepositById(uuid) } returns null

        val response = client.get("/api/deposit") {
            setupRequest()
            parameter("id", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There is no deposit with such ID")
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
            userId = userId,
            wareId = wareId,
            timestamp = "2024-05-04 16:37:33.870626-03"
        )

        val response = client.get("/api/deposit") {
            setupRequest()
            parameter("id", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting by unexistent userId, should return NotFound`() = testApplication {
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        setup()

        coEvery { service.getDeposits(userId, null) } returns listOf()

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("userId", userId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There are no deposits for such filters")
    }

    @Test
    fun `Getting deposit by userId correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val wareId = "390fddcd-d238-442a-9c44-8c1a3c29cd4e"

        setup()

        coEvery { service.getDeposits(userId, null) } returns listOf(
            DepositData(
                uuid = uuid,
                quantity = 500.0,
                userId = userId,
                wareId = wareId,
                timestamp = "2024-05-04 16:37:33.870626-03"
            )
        )

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("userId", userId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting by unexistent wareId, should return NotFound`() = testApplication {
        val wareId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        setup()

        coEvery { service.getDeposits(null, wareId) } returns listOf()

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("wareId", wareId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There are no deposits for such filters")
    }

    @Test
    fun `Getting deposit by wareId correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val wareId = "390fddcd-d238-442a-9c44-8c1a3c29cd4e"

        setup()

        coEvery { service.getDeposits(null, wareId) } returns listOf(
            DepositData(
                uuid = uuid,
                quantity = 500.0,
                userId = userId,
                wareId = wareId,
                timestamp = "2024-05-04 16:37:33.870626-03"
            )
        )

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("wareId", wareId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting deposits with unexistent order, should return BadRequest`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val deposit1 = depositData1(uuid)
        val deposit2 = depositData2(uuid)
        val deposit3 = depositData3(uuid)

        setup()

        coEvery { service.getDeposits(null, null) } returns listOf(
            deposit1,
            deposit2,
            deposit3,
        )

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("order", "unexistent")
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
        assertThat(response.bodyAsText()).isEqualTo("Order should be: [asc, desc]")
    }

    @Test
    fun `Getting deposits with unexistent sorting type, should return BadRequest`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val deposit1 = depositData1(uuid)
        val deposit2 = depositData2(uuid)
        val deposit3 = depositData3(uuid)

        setup()

        coEvery { service.getDeposits(null, null) } returns listOf(
            deposit1,
            deposit2,
            deposit3,
        )

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("sortBy", "unexistent")
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
        assertThat(response.bodyAsText()).isEqualTo("Sorting should be: [userId, wareId, quantity, timestamp]")
    }

    @Test
    fun `Getting deposits sorting by ware descending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val deposit1 = depositData1(uuid)
        val deposit2 = depositData2(uuid)
        val deposit3 = depositData3(uuid)

        setup()

        coEvery { service.getDeposits(null, null) } returns listOf(
            deposit1,
            deposit2,
            deposit3,
        )

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("sortBy", "wareId")
            parameter("order", "desc")
        }

        val result = Json.decodeFromString<List<DepositData>>(response.bodyAsText())
        assertThat(result[0].wareId).isEqualTo("75ba8951-d1cd-46cb-bde7-39caa35a8929")
        assertThat(result[1].wareId).isEqualTo("61e43d8b-a532-4f36-af85-1566f8bbb6f7")
        assertThat(result[2].wareId).isEqualTo("61e43d8b-a532-4f36-af85-1566f8bbb6f7")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting deposits sorting by ware ascending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val deposit1 = depositData1(uuid)
        val deposit2 = depositData2(uuid)
        val deposit3 = depositData3(uuid)

        setup()

        coEvery { service.getDeposits(null, null) } returns listOf(
            deposit1,
            deposit2,
            deposit3,
        )

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("sortBy", "wareId")
            parameter("order", "asc")
        }

        val result = Json.decodeFromString<List<DepositData>>(response.bodyAsText())
        assertThat(result[0].wareId).isEqualTo("61e43d8b-a532-4f36-af85-1566f8bbb6f7")
        assertThat(result[1].wareId).isEqualTo("61e43d8b-a532-4f36-af85-1566f8bbb6f7")
        assertThat(result[2].wareId).isEqualTo("75ba8951-d1cd-46cb-bde7-39caa35a8929")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting deposits sorting by user descending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val deposit1 = depositData1(uuid)
        val deposit2 = depositData2(uuid)
        val deposit3 = depositData3(uuid)

        setup()

        coEvery { service.getDeposits(null, null) } returns listOf(
            deposit1,
            deposit2,
            deposit3,
        )

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("sortBy", "userId")
            parameter("order", "desc")
        }

        val result = Json.decodeFromString<List<DepositData>>(response.bodyAsText())
        assertThat(result[0].userId).isEqualTo("75ba8951-d1cd-46cb-bde7-39caa35a8929")
        assertThat(result[1].userId).isEqualTo("75ba8951-d1cd-46cb-bde7-39caa35a8929")
        assertThat(result[2].userId).isEqualTo("61e43d8b-a532-4f36-af85-1566f8bbb6f7")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting deposits sorting by user ascending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val deposit1 = depositData1(uuid)
        val deposit2 = depositData2(uuid)
        val deposit3 = depositData3(uuid)

        setup()

        coEvery { service.getDeposits(null, null) } returns listOf(
            deposit1,
            deposit2,
            deposit3,
        )

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("sortBy", "userId")
            parameter("order", "asc")
        }

        val result = Json.decodeFromString<List<DepositData>>(response.bodyAsText())
        assertThat(result[0].userId).isEqualTo("61e43d8b-a532-4f36-af85-1566f8bbb6f7")
        assertThat(result[1].userId).isEqualTo("75ba8951-d1cd-46cb-bde7-39caa35a8929")
        assertThat(result[2].userId).isEqualTo("75ba8951-d1cd-46cb-bde7-39caa35a8929")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting deposits sorting by quantity descending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val deposit1 = depositData1(uuid)
        val deposit2 = depositData2(uuid)
        val deposit3 = depositData3(uuid)

        setup()

        coEvery { service.getDeposits(null, null) } returns listOf(
            deposit1,
            deposit2,
            deposit3,
        )

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("sortBy", "quantity")
            parameter("order", "desc")
        }

        val result = Json.decodeFromString<List<DepositData>>(response.bodyAsText())
        assertThat(result[0].quantity).isEqualTo(590.0)
        assertThat(result[1].quantity).isEqualTo(500.0)
        assertThat(result[2].quantity).isEqualTo(50.0)
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting deposits sorting by quantity ascending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val deposit1 = depositData1(uuid)
        val deposit2 = depositData2(uuid)
        val deposit3 = depositData3(uuid)

        setup()

        coEvery { service.getDeposits(null, null) } returns listOf(
            deposit1,
            deposit2,
            deposit3,
        )

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("sortBy", "quantity")
            parameter("order", "asc")
        }

        val result = Json.decodeFromString<List<DepositData>>(response.bodyAsText())
        assertThat(result[0].quantity).isEqualTo(50.0)
        assertThat(result[1].quantity).isEqualTo(500.0)
        assertThat(result[2].quantity).isEqualTo(590.0)
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting deposits sorting by timestamp descending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val deposit1 = depositData1(uuid)
        val deposit2 = depositData2(uuid)
        val deposit3 = depositData3(uuid)

        setup()

        coEvery { service.getDeposits(null, null) } returns listOf(
            deposit1,
            deposit2,
            deposit3,
        )

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("sortBy", "timestamp")
            parameter("order", "desc")
        }

        val result = Json.decodeFromString<List<DepositData>>(response.bodyAsText())
        assertThat(result[0].timestamp).isEqualTo("2024-06-04 16:37:33.870626-03")
        assertThat(result[1].timestamp).isEqualTo("2024-05-05 16:37:33.870626-03")
        assertThat(result[2].timestamp).isEqualTo("2024-05-04 16:37:33.870626-03")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting deposits sorting by timestamp ascending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d43-95ae-ba43dbd46b47"

        val deposit1 = depositData1(uuid)
        val deposit2 = depositData2(uuid)
        val deposit3 = depositData3(uuid)

        setup()

        coEvery { service.getDeposits(null, null) } returns listOf(
            deposit1,
            deposit2,
            deposit3,
        )

        val response = client.get("/api/deposits") {
            setupRequest()
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
        }

        val result = Json.decodeFromString<List<DepositData>>(response.bodyAsText())
        assertThat(result[0].timestamp).isEqualTo("2024-05-04 16:37:33.870626-03")
        assertThat(result[1].timestamp).isEqualTo("2024-05-05 16:37:33.870626-03")
        assertThat(result[2].timestamp).isEqualTo("2024-06-04 16:37:33.870626-03")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    private fun depositData3(uuid: String) = DepositData(
        uuid = uuid,
        userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929",
        wareId = "61e43d8b-a532-4f36-af85-1566f8bbb6f7",
        quantity = 590.0,
        timestamp = "2024-05-05 16:37:33.870626-03"
    )

    private fun depositData2(uuid: String) = DepositData(
        uuid = uuid,
        userId = "61e43d8b-a532-4f36-af85-1566f8bbb6f7",
        wareId = "75ba8951-d1cd-46cb-bde7-39caa35a8929",
        quantity = 50.0,
        timestamp = "2024-06-04 16:37:33.870626-03"
    )

    private fun depositData1(uuid: String) = DepositData(
        uuid = uuid,
        userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929",
        wareId = "61e43d8b-a532-4f36-af85-1566f8bbb6f7",
        quantity = 500.0,
        timestamp = "2024-05-04 16:37:33.870626-03"
    )

    private fun ApplicationTestBuilder.setup() {
        setupApplication {
            install(Koin) {
                slf4jLogger()
                modules(depositModule)
            }
            depositRoutes()
        }
    }
    private val depositModule = module {
        single<DepositRepository> {
            DepositRepositoryImpl(service)
        }
        single {
            DepositUseCases(
                createDepositUseCase = CreateDepositUseCase(get()),
                getDepositByIDUseCase = GetDepositByIDUseCase(get()),
                getDepositsUseCase = GetDepositsUseCase(get()),
            )
        }
    }
}
