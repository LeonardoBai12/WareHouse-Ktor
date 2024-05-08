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
import io.lb.warehouse.deposit.domain.use_cases.GetDepositsByUserIdUseCase
import io.lb.warehouse.deposit.domain.use_cases.GetDepositsByWareIdUseCase
import io.lb.warehouse.util.setupApplication
import io.lb.warehouse.util.setupRequest
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
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
    fun `Getting by userId with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.get("/api/depositsCreatedByUser") {
            setupRequest()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Getting by unexistent userId, should return NotFound`() = testApplication {
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        setup()

        coEvery { service.getDepositsByUserId(userId) } returns listOf()

        val response = client.get("/api/depositsCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There are no deposits for such user")
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
                userId = userId,
                wareId = wareId,
                timestamp = "2024-05-04 16:37:33.870626-03"
            )
        )

        val response = client.get("/api/depositsCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting by wareId with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.get("/api/depositsByWareId") {
            setupRequest()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Getting by unexistent wareId, should return NotFound`() = testApplication {
        val wareId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        setup()

        coEvery { service.getDepositsByWareId(wareId) } returns listOf()

        val response = client.get("/api/depositsByWareId") {
            setupRequest()
            parameter("wareId", wareId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There are no deposits for such ware")
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
                userId = userId,
                wareId = wareId,
                timestamp = "2024-05-04 16:37:33.870626-03"
            )
        )

        val response = client.get("/api/depositsByWareId") {
            setupRequest()
            parameter("wareId", wareId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

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
                getDepositsByUserIdUseCase = GetDepositsByUserIdUseCase(get()),
                getDepositsByWareIdUseCase = GetDepositsByWareIdUseCase(get()),
            )
        }
    }
}
