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
import io.lb.warehouse.withdraw.domain.use_cases.GetWithdrawsByUserIdUseCase
import io.lb.warehouse.withdraw.domain.use_cases.GetWithdrawsByWareIdUseCase
import io.lb.warehouse.withdraw.domain.use_cases.WithdrawUseCases
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
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
    fun `Getting by userId with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.get("/api/withdrawsCreatedByUser") {
            setupRequest()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Getting by unexistent userId, should return NotFound`() = testApplication {
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        setup()

        coEvery { service.getWithdrawsByUserId(userId) } returns listOf()

        val response = client.get("/api/withdrawsCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There are no withdraws for such user")
    }

    @Test
    fun `Getting withdraw by userId correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val wareId = "390fddcd-d238-442a-9c44-8c1a3c29cd4e"

        setup()

        coEvery { service.getWithdrawsByUserId(userId) } returns listOf(
            WithdrawData(
                uuid = uuid,
                quantity = 500.0,
                userId = userId,
                wareId = wareId,
                timestamp = "2024-05-04 16:37:33.870626-03"
            )
        )

        val response = client.get("/api/withdrawsCreatedByUser") {
            setupRequest()
            parameter("userId", userId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting by wareId with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.get("/api/withdrawsByWareId") {
            setupRequest()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Getting by unexistent wareId, should return NotFound`() = testApplication {
        val wareId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        setup()

        coEvery { service.getWithdrawsByWareId(wareId) } returns listOf()

        val response = client.get("/api/withdrawsByWareId") {
            setupRequest()
            parameter("wareId", wareId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There are no withdraws for such ware")
    }

    @Test
    fun `Getting withdraw by wareId correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val wareId = "390fddcd-d238-442a-9c44-8c1a3c29cd4e"

        setup()

        coEvery { service.getWithdrawsByWareId(wareId) } returns listOf(
            WithdrawData(
                uuid = uuid,
                quantity = 500.0,
                userId = userId,
                wareId = wareId,
                timestamp = "2024-05-04 16:37:33.870626-03"
            )
        )

        val response = client.get("/api/withdrawsByWareId") {
            setupRequest()
            parameter("wareId", wareId)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

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
                getWithdrawsByUserIdUseCase = GetWithdrawsByUserIdUseCase(get()),
                getWithdrawsByWareIdUseCase = GetWithdrawsByWareIdUseCase(get()),
            )
        }
    }
}
