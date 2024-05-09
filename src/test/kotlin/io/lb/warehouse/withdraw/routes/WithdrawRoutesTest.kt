package io.lb.warehouse.withdraw.routes

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
import io.lb.warehouse.util.setupApplication
import io.lb.warehouse.util.setupRequest
import io.lb.warehouse.withdraw.data.model.WithdrawData
import io.lb.warehouse.withdraw.data.repository.WithdrawRepositoryImpl
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseServiceImpl
import io.lb.warehouse.withdraw.domain.repository.WithdrawRepository
import io.lb.warehouse.withdraw.domain.use_cases.CreateWithdrawUseCase
import io.lb.warehouse.withdraw.domain.use_cases.GetWithdrawByIDUseCase
import io.lb.warehouse.withdraw.domain.use_cases.GetWithdrawsUseCase
import io.lb.warehouse.withdraw.domain.use_cases.WithdrawUseCases
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

class WithdrawRoutesTest {
    private val service: WithdrawDatabaseServiceImpl = mockk()

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Creating withdraw with typos, should return BadRequest`() = testApplication {
        setup()

        val response = client.post("/api/createWithdraw") {
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
    fun `Creating withdraw correctly, should return Created`() = testApplication {
        setup()
        coEvery { service.insertWithdraw(any()) } returns ""

        val response = client.post("/api/createWithdraw") {
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

        val response = client.get("/api/withdraw") {
            setupRequest()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Getting unexistent withdraw, should return NotFound`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getWithdrawById(uuid) } returns null

        val response = client.get("/api/withdraw") {
            setupRequest()
            parameter("id", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There is no withdraw with such ID")
    }

    @Test
    fun `Getting withdraw correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val wareId = "390fddcd-d238-442a-9c44-8c1a3c29cd4e"

        setup()

        coEvery { service.getWithdrawById(uuid) } returns WithdrawData(
            uuid = uuid,
            quantity = 500.0,
            userId = userId,
            wareId = wareId,
            timestamp = "2024-05-04 16:37:33.870626-03"
        )

        val response = client.get("/api/withdraw") {
            setupRequest()
            parameter("id", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting by unexistent userId, should return NotFound`() = testApplication {
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        setup()

        coEvery { service.getWithdraws(userId, null) } returns listOf()

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("userId", userId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There are no withdraws for such filters")
    }

    @Test
    fun `Getting withdraw by userId correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val wareId = "390fddcd-d238-442a-9c44-8c1a3c29cd4e"

        setup()

        coEvery { service.getWithdraws(userId, null) } returns listOf(
            WithdrawData(
                uuid = uuid,
                quantity = 500.0,
                userId = userId,
                wareId = wareId,
                timestamp = "2024-05-04 16:37:33.870626-03"
            )
        )

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("userId", userId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting by unexistent wareId, should return NotFound`() = testApplication {
        val wareId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        setup()

        coEvery { service.getWithdraws(null, wareId) } returns listOf()

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("wareId", wareId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There are no withdraws for such filters")
    }

    @Test
    fun `Getting withdraw by wareId correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val wareId = "390fddcd-d238-442a-9c44-8c1a3c29cd4e"

        setup()

        coEvery { service.getWithdraws(null, wareId) } returns listOf(
            WithdrawData(
                uuid = uuid,
                quantity = 500.0,
                userId = userId,
                wareId = wareId,
                timestamp = "2024-05-04 16:37:33.870626-03"
            )
        )

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("wareId", wareId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting withdraws with unexistent order, should return BadRequest`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val withdraw1 = withdrawData1(uuid)
        val withdraw2 = withdrawData2(uuid)
        val withdraw3 = withdrawData3(uuid)

        setup()

        coEvery { service.getWithdraws(null, null) } returns listOf(
            withdraw1,
            withdraw2,
            withdraw3,
        )

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("order", "unexistent")
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
        assertThat(response.bodyAsText()).isEqualTo("Order should be: [asc, desc]")
    }

    @Test
    fun `Getting withdraws with unexistent sorting type, should return BadRequest`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val withdraw1 = withdrawData1(uuid)
        val withdraw2 = withdrawData2(uuid)
        val withdraw3 = withdrawData3(uuid)

        setup()

        coEvery { service.getWithdraws(null, null) } returns listOf(
            withdraw1,
            withdraw2,
            withdraw3,
        )

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("sortBy", "unexistent")
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
        assertThat(response.bodyAsText()).isEqualTo("Sorting should be: [userId, wareId, quantity, timestamp]")
    }

    @Test
    fun `Getting withdraws sorting by ware descending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val withdraw1 = withdrawData1(uuid)
        val withdraw2 = withdrawData2(uuid)
        val withdraw3 = withdrawData3(uuid)

        setup()

        coEvery { service.getWithdraws(null, null) } returns listOf(
            withdraw1,
            withdraw2,
            withdraw3,
        )

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("sortBy", "wareId")
            parameter("order", "desc")
        }

        val result = Json.decodeFromString<List<WithdrawData>>(response.bodyAsText())
        assertThat(result[0].wareId).isEqualTo("75ba8951-d1cd-46cb-bde7-39caa35a8929")
        assertThat(result[1].wareId).isEqualTo("61e43d8b-a532-4f36-af85-1566f8bbb6f7")
        assertThat(result[2].wareId).isEqualTo("61e43d8b-a532-4f36-af85-1566f8bbb6f7")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting withdraws sorting by ware ascending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val withdraw1 = withdrawData1(uuid)
        val withdraw2 = withdrawData2(uuid)
        val withdraw3 = withdrawData3(uuid)

        setup()

        coEvery { service.getWithdraws(null, null) } returns listOf(
            withdraw1,
            withdraw2,
            withdraw3,
        )

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("sortBy", "wareId")
            parameter("order", "asc")
        }

        val result = Json.decodeFromString<List<WithdrawData>>(response.bodyAsText())
        assertThat(result[0].wareId).isEqualTo("61e43d8b-a532-4f36-af85-1566f8bbb6f7")
        assertThat(result[1].wareId).isEqualTo("61e43d8b-a532-4f36-af85-1566f8bbb6f7")
        assertThat(result[2].wareId).isEqualTo("75ba8951-d1cd-46cb-bde7-39caa35a8929")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting withdraws sorting by user descending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val withdraw1 = withdrawData1(uuid)
        val withdraw2 = withdrawData2(uuid)
        val withdraw3 = withdrawData3(uuid)

        setup()

        coEvery { service.getWithdraws(null, null) } returns listOf(
            withdraw1,
            withdraw2,
            withdraw3,
        )

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("sortBy", "userId")
            parameter("order", "desc")
        }

        val result = Json.decodeFromString<List<WithdrawData>>(response.bodyAsText())
        assertThat(result[0].userId).isEqualTo("75ba8951-d1cd-46cb-bde7-39caa35a8929")
        assertThat(result[1].userId).isEqualTo("75ba8951-d1cd-46cb-bde7-39caa35a8929")
        assertThat(result[2].userId).isEqualTo("61e43d8b-a532-4f36-af85-1566f8bbb6f7")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting withdraws sorting by user ascending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val withdraw1 = withdrawData1(uuid)
        val withdraw2 = withdrawData2(uuid)
        val withdraw3 = withdrawData3(uuid)

        setup()

        coEvery { service.getWithdraws(null, null) } returns listOf(
            withdraw1,
            withdraw2,
            withdraw3,
        )

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("sortBy", "userId")
            parameter("order", "asc")
        }

        val result = Json.decodeFromString<List<WithdrawData>>(response.bodyAsText())
        assertThat(result[0].userId).isEqualTo("61e43d8b-a532-4f36-af85-1566f8bbb6f7")
        assertThat(result[1].userId).isEqualTo("75ba8951-d1cd-46cb-bde7-39caa35a8929")
        assertThat(result[2].userId).isEqualTo("75ba8951-d1cd-46cb-bde7-39caa35a8929")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting withdraws sorting by quantity descending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val withdraw1 = withdrawData1(uuid)
        val withdraw2 = withdrawData2(uuid)
        val withdraw3 = withdrawData3(uuid)

        setup()

        coEvery { service.getWithdraws(null, null) } returns listOf(
            withdraw1,
            withdraw2,
            withdraw3,
        )

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("sortBy", "quantity")
            parameter("order", "desc")
        }

        val result = Json.decodeFromString<List<WithdrawData>>(response.bodyAsText())
        assertThat(result[0].quantity).isEqualTo(590.0)
        assertThat(result[1].quantity).isEqualTo(500.0)
        assertThat(result[2].quantity).isEqualTo(50.0)
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting withdraws sorting by quantity ascending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val withdraw1 = withdrawData1(uuid)
        val withdraw2 = withdrawData2(uuid)
        val withdraw3 = withdrawData3(uuid)

        setup()

        coEvery { service.getWithdraws(null, null) } returns listOf(
            withdraw1,
            withdraw2,
            withdraw3,
        )

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("sortBy", "quantity")
            parameter("order", "asc")
        }

        val result = Json.decodeFromString<List<WithdrawData>>(response.bodyAsText())
        assertThat(result[0].quantity).isEqualTo(50.0)
        assertThat(result[1].quantity).isEqualTo(500.0)
        assertThat(result[2].quantity).isEqualTo(590.0)
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting withdraws sorting by timestamp descending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"

        val withdraw1 = withdrawData1(uuid)
        val withdraw2 = withdrawData2(uuid)
        val withdraw3 = withdrawData3(uuid)

        setup()

        coEvery { service.getWithdraws(null, null) } returns listOf(
            withdraw1,
            withdraw2,
            withdraw3,
        )

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("sortBy", "timestamp")
            parameter("order", "desc")
        }

        val result = Json.decodeFromString<List<WithdrawData>>(response.bodyAsText())
        assertThat(result[0].timestamp).isEqualTo("2024-06-04 16:37:33.870626-03")
        assertThat(result[1].timestamp).isEqualTo("2024-05-05 16:37:33.870626-03")
        assertThat(result[2].timestamp).isEqualTo("2024-05-04 16:37:33.870626-03")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting withdraws sorting by timestamp ascending, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d43-95ae-ba43dbd46b47"

        val withdraw1 = withdrawData1(uuid)
        val withdraw2 = withdrawData2(uuid)
        val withdraw3 = withdrawData3(uuid)

        setup()

        coEvery { service.getWithdraws(null, null) } returns listOf(
            withdraw1,
            withdraw2,
            withdraw3,
        )

        val response = client.get("/api/withdraws") {
            setupRequest()
            parameter("sortBy", "timestamp")
            parameter("order", "asc")
        }

        val result = Json.decodeFromString<List<WithdrawData>>(response.bodyAsText())
        assertThat(result[0].timestamp).isEqualTo("2024-05-04 16:37:33.870626-03")
        assertThat(result[1].timestamp).isEqualTo("2024-05-05 16:37:33.870626-03")
        assertThat(result[2].timestamp).isEqualTo("2024-06-04 16:37:33.870626-03")
        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    private fun withdrawData3(uuid: String) = WithdrawData(
        uuid = uuid,
        userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929",
        wareId = "61e43d8b-a532-4f36-af85-1566f8bbb6f7",
        quantity = 590.0,
        timestamp = "2024-05-05 16:37:33.870626-03"
    )

    private fun withdrawData2(uuid: String) = WithdrawData(
        uuid = uuid,
        userId = "61e43d8b-a532-4f36-af85-1566f8bbb6f7",
        wareId = "75ba8951-d1cd-46cb-bde7-39caa35a8929",
        quantity = 50.0,
        timestamp = "2024-06-04 16:37:33.870626-03"
    )

    private fun withdrawData1(uuid: String) = WithdrawData(
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
                modules(withdrawModule)
            }
            withdrawRoutes()
        }
    }
    private val withdrawModule = module {
        single<WithdrawRepository> {
            WithdrawRepositoryImpl(service)
        }
        single {
            WithdrawUseCases(
                createWithdrawUseCase = CreateWithdrawUseCase(get()),
                getWithdrawByIDUseCase = GetWithdrawByIDUseCase(get()),
                getWithdrawsUseCase = GetWithdrawsUseCase(get()),
            )
        }
    }
}
