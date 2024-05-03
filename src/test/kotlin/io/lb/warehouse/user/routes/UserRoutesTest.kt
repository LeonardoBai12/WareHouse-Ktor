package io.lb.warehouse.user.routes

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.lb.warehouse.core.extensions.encrypt
import io.lb.warehouse.user.data.model.UserData
import io.lb.warehouse.user.data.service.UserDatabaseService
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class UserRoutesTest {
    private val service: UserDatabaseService = mockk()

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Creating user with an email already in use, should return Conflict`() = testApplication {
        setup()

        coEvery { service.isEmailAlreadyInUse(any()) } returns true

        val response = client.post("/api/createUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """
                {
                    "userName": "testuser",
                    "password": "testpassword",
                    "email": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
    }

    @Test
    fun `Creating user with typos, should return BadRequest`() = testApplication {
        setup()

        val response = client.post("/api/createUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """
                {
                    "userName": "testuser",
                    "password_typo": "testpassword",
                    "email_typo": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Creating user correctly, should return Created`() = testApplication {
        setup()

        coEvery { service.isEmailAlreadyInUse(any()) } returns false
        coEvery { service.createUser(any()) } returns 1

        val response = client.post("/api/createUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """
                {
                    "userName": "testuser",
                    "password": "testpassword",
                    "email": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Created)
    }

    @Test
    fun `Updating unexistent user, should return NotFound`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns null

        val response = client.put("/api/updateUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
            setBody(
                """
                {
                    "userName": "testuser",
                    "password": "testpassword",
                    "email": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }

    @Test
    fun `Updating without id param, should return BadRequest`() = testApplication {
        setup()

        val response = client.put("/api/updateUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """
                {
                    "userName": "testuser",
                    "password": "testpassword",
                    "email": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Updating user with an email already in use, should return Conflict`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.isEmailAlreadyInUse(any()) } returns true
        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.put("/api/updateUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
            setBody(
                """
                {
                    "userName": "testuser",
                    "password": "testpassword",
                    "email": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
    }

    @Test
    fun `Updating user with wrong password, should return Unauthorized`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.isEmailAlreadyInUse(any()) } returns false
        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.put("/api/updateUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
            setBody(
                """
                {
                    "userName": "testuser",
                    "password": "wrongpassword",
                    "email": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `Updating user with no password, should return Unauthorized`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.isEmailAlreadyInUse(any()) } returns false
        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.put("/api/updateUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
            setBody(
                """
                {
                    "userName": "testuser",
                    "password": "",
                    "email": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `Updating user with typos, should return BadRequest`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.isEmailAlreadyInUse(any()) } returns false
        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.put("/api/updateUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
            setBody(
                """
                {
                    "userName": "testuser",
                    "password_typo": "testpassword",
                    "email_typo": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Updating user correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.isEmailAlreadyInUse(any()) } returns false
        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )
        coEvery { service.updateUser(any()) } returns 1

        val response = client.put("/api/updateUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
            setBody(
                """
                {
                    "userName": "testuser",
                    "password": "testpassword",
                    "email": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Deleting with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.delete("/api/deleteUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Deleting unexistent user, should return NotFound`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns null

        val response = client.delete("/api/deleteUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }

    @Test
    fun `Deleting user correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )
        coEvery { service.deleteUser(any()) } returns 1

        val response = client.delete("/api/deleteUser") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.get("/api/user") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Getting unexistent user, should return NotFound`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns null

        val response = client.get("/api/user") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
    }

    @Test
    fun `Getting user correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.get("/api/user") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Updating password without id param, should return BadRequest`() = testApplication {
        setup()

        val response = client.put("/api/updatePassword") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """
                {
                    "password": "testpassword",
                    "newPassword": "newPassword"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Updating password with wrong password, should return Unauthorized`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.put("/api/updatePassword") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
            setBody(
                """
                {
                    "password": "wrongpassword",
                    "newPassword": "newPassword"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `Updating password with no password, should return Unauthorized`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.put("/api/updatePassword") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
            setBody(
                """
                {
                    "password": "",
                    "newPassword": "newPassword"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
    }

    @Test
    fun `Updating password with typos, should return BadRequest`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.put("/api/updatePassword") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
            setBody(
                """
                {
                    "passworld": "testpassword",
                    "newPassworld": "newPassword"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Updating password correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )
        coEvery { service.updatePassword(any(), any()) } returns 1

        val response = client.put("/api/updatePassword") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            parameter("userId", uuid)
            setBody(
                """
                {
                    "password": "testpassword",
                    "newPassword": "newPassword"
                }
                """.trimIndent()
            )
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
            userRoutes(service)
        }
    }
}
