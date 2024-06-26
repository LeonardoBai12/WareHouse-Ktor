package io.lb.warehouse.user.routes

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
import io.lb.warehouse.core.extensions.encrypt
import io.lb.warehouse.security.data.model.TokenConfig
import io.lb.warehouse.user.data.model.UserData
import io.lb.warehouse.user.data.repository.UserRepositoryImpl
import io.lb.warehouse.user.data.service.UserDatabaseServiceImpl
import io.lb.warehouse.user.domain.repository.UserRepository
import io.lb.warehouse.user.domain.use_cases.DeleteUserUseCase
import io.lb.warehouse.user.domain.use_cases.GetUserByIdUseCase
import io.lb.warehouse.user.domain.use_cases.LoginUseCase
import io.lb.warehouse.user.domain.use_cases.SignUpUseCase
import io.lb.warehouse.user.domain.use_cases.UpdatePasswordUseCase
import io.lb.warehouse.user.domain.use_cases.UpdateUserUseCase
import io.lb.warehouse.user.domain.use_cases.UserUseCases
import io.lb.warehouse.util.configureSession
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

class UserRoutesTest {
    private val service: UserDatabaseServiceImpl = mockk()

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Creating user with no name, should return Conflict`() = testApplication {
        setup(bypass = false)

        coEvery { service.isEmailAlreadyInUse(any()) } returns false

        val response = client.post("/api/signUp") {
            setupRequest()
            setBody(
                """
                {
                    "userName": "  ",
                    "password": "password",
                    "email": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.bodyAsText()).isEqualTo("User must have a name.")
    }

    @Test
    fun `Creating user with invalid email, should return Conflict`() = testApplication {
        setup(bypass = false)

        coEvery { service.isEmailAlreadyInUse(any()) } returns false

        val response = client.post("/api/signUp") {
            setupRequest()
            setBody(
                """
                {
                    "userName": "Valid name",
                    "password": "password",
                    "email": "testexample.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.bodyAsText()).isEqualTo("Invalid email.")
    }

    @Test
    fun `Creating user there is already an user logged in, should return Conflict`() = testApplication {
        setup(userId = "d5745279-6bbe-4d73-95ae-ba43dbd46b47")

        coEvery { service.isEmailAlreadyInUse(any()) } returns false

        val response = client.post("/api/signUp") {
            setupRequest()
            setBody(
                """
                {
                    "userName": "testuser",
                    "password": "password",
                    "email": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.bodyAsText()).isEqualTo("There is already an user logged in.")
    }

    @Test
    fun `Creating user with short password, should return Conflict`() = testApplication {
        setup(bypass = false)

        coEvery { service.isEmailAlreadyInUse(any()) } returns false

        val response = client.post("/api/signUp") {
            setupRequest()
            setBody(
                """
                {
                    "userName": "testuser",
                    "password": "short",
                    "email": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.bodyAsText()).isEqualTo("Password must have more than 8 characters.")
    }

    @Test
    fun `Creating user with an email already in use, should return Conflict`() = testApplication {
        setup(bypass = false)

        coEvery { service.isEmailAlreadyInUse(any()) } returns true

        val response = client.post("/api/signUp") {
            setupRequest()
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
        assertThat(response.bodyAsText()).isEqualTo("Email already in use by another user.")
    }

    @Test
    fun `Creating user with typos, should return BadRequest`() = testApplication {
        setup(bypass = false)

        val response = client.post("/api/signUp") {
            setupRequest()
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
        setup(bypass = false)

        coEvery { service.isEmailAlreadyInUse(any()) } returns false
        coEvery { service.createUser(any()) } returns 1

        val response = client.post("/api/signUp") {
            setupRequest()
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
        client.get("/api/logout")
    }

    @Test
    fun `Updating user other than theirself, should return Unauthorized`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val loggedUUID = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        setup(userId = loggedUUID)

        coEvery { service.isEmailAlreadyInUse(any()) } returns true
        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.put("/api/updateUser") {
            setupRequest()
            parameter("userId", uuid)
            setBody(
                """
                {
                    "userName": "Name Example",
                    "password": "testpassword",
                    "email": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
        assertThat(response.bodyAsText()).isEqualTo("You are not authorized to update this user.")
    }

    @Test
    fun `Updating user with no name, should return Conflict`() = testApplication {
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
            setupRequest()
            parameter("userId", uuid)
            setBody(
                """
                {
                    "userName": "    ",
                    "password": "testpassword",
                    "email": "test@example.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.bodyAsText()).isEqualTo("User must have a name.")
    }

    @Test
    fun `Updating user with invalid email, should return Conflict`() = testApplication {
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
            setupRequest()
            parameter("userId", uuid)
            setBody(
                """
                {
                    "userName": "name",
                    "password": "testpassword",
                    "email": "testexample.com",
                    "profilePictureUrl": "http://example.com/pic.jpg"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.bodyAsText()).isEqualTo("Invalid email.")
    }

    @Test
    fun `Updating unexistent user, should return NotFound`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns null
        coEvery { service.isEmailAlreadyInUse(any()) } returns false

        val response = client.put("/api/updateUser") {
            setupRequest()
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
        assertThat(response.bodyAsText()).isEqualTo("There is no user with such ID")
    }

    @Test
    fun `Updating without id param, should return BadRequest`() = testApplication {
        setup()

        val response = client.put("/api/updateUser") {
            setupRequest()
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
            setupRequest()
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
        assertThat(response.bodyAsText()).isEqualTo("Email already in use by another user.")
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
            setupRequest()
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
        assertThat(response.bodyAsText()).isEqualTo("Invalid password")
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
            setupRequest()
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
        assertThat(response.bodyAsText()).isEqualTo("Invalid password")
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
            setupRequest()
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
            setupRequest()
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
    fun `Deleting user other than theirself, should return Unauthorized`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val loggedUUID = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        setup(userId = loggedUUID)

        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.delete("/api/deleteUser") {
            setupRequest()
            parameter("userId", uuid)
            setBody(
                """
                {
                    "password": "testpassword"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
        assertThat(response.bodyAsText()).isEqualTo("You are not authorized to update this user.")
    }

    @Test
    fun `Deleting user with wrong password, should return Unauthorized`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.delete("/api/deleteUser") {
            setupRequest()
            parameter("userId", uuid)
            setBody(
                """
                {
                    "password": "wrongpassword"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
        assertThat(response.bodyAsText()).isEqualTo("Invalid password")
    }

    @Test
    fun `Deleting password with no password, should return Unauthorized`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.delete("/api/deleteUser") {
            setupRequest()
            parameter("userId", uuid)
            setBody(
                """
                {
                    "password": ""
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
        assertThat(response.bodyAsText()).isEqualTo("Invalid password")
    }

    @Test
    fun `Deleting password with typos, should return BadRequest`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.delete("/api/deleteUser") {
            setupRequest()
            parameter("userId", uuid)
            setBody(
                """
                {
                    "passworld": "testpassword"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Deleting with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.delete("/api/deleteUser") {
            setupRequest()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Deleting unexistent user, should return NotFound`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns null

        val response = client.delete("/api/deleteUser") {
            setupRequest()
            parameter("userId", uuid)
            setBody(
                """
                {
                    "password": "newPassword"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There is no user with such ID")
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
            setupRequest()
            parameter("userId", uuid)
            setBody(
                """
                {
                    "password": "testpassword"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Getting with no id param, should return BadRequesst`() = testApplication {
        setup()

        val response = client.get("/api/user") {
            setupRequest()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `Getting unexistent user, should return NotFound`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns null

        val response = client.get("/api/user") {
            setupRequest()
            parameter("userId", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There is no user with such ID")
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
            setupRequest()
            parameter("userId", uuid)
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Updating password with a short password, should return Conflict`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup()

        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.put("/api/updatePassword") {
            setupRequest()
            parameter("userId", uuid)
            setBody(
                """
                {
                    "password": "testpassword",
                    "newPassword": "short"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.bodyAsText()).isEqualTo("Password must have more than 8 characters.")
    }

    @Test
    fun `Updating password other than theirself, should return Unauthorized`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        val loggedUUID = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        setup(userId = loggedUUID)

        coEvery { service.getUserById(uuid) } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "testold@example.com"
        )

        val response = client.put("/api/updatePassword") {
            setupRequest()
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

        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
        assertThat(response.bodyAsText()).isEqualTo("You are not authorized to update this user.")
    }

    @Test
    fun `Updating password without id param, should return BadRequest`() = testApplication {
        setup()

        val response = client.put("/api/updatePassword") {
            setupRequest()
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
            setupRequest()
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
        assertThat(response.bodyAsText()).isEqualTo("Invalid password")
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
            setupRequest()
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
        assertThat(response.bodyAsText()).isEqualTo("Invalid password")
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
            setupRequest()
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
            setupRequest()
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

    @Test
    fun `Loggin in with unexistent user, should return NotFound`() = testApplication {
        setup(bypass = false)

        coEvery { service.getUserByEmail("unexistent@email.com") } returns null

        val response = client.get("/api/login") {
            setupRequest()
            setBody(
                """
                {
                    "email": "unexistent@email.com",
                    "password": "wrongpassword"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.NotFound)
        assertThat(response.bodyAsText()).isEqualTo("There is no user with such email")
    }

    @Test
    fun `Loggin in with user already logged in, should return Conflict`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup(userId = uuid)

        val response = client.get("/api/login") {
            setupRequest()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Conflict)
        assertThat(response.bodyAsText()).isEqualTo("There is already an user logged in.")
    }

    @Test
    fun `Loggin in with wrong password, should return Unauthorized`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup(bypass = false)

        coEvery { service.getUserByEmail("example@email.com") } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "example@email.com"
        )

        val response = client.get("/api/login") {
            setupRequest()
            setBody(
                """
                {
                    "email": "example@email.com",
                    "password": "wrongpassword"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
        assertThat(response.bodyAsText()).isEqualTo("Invalid password")
    }

    @Test
    fun `Loggin in correctly, should return OK`() = testApplication {
        val uuid = "d5745279-6bbe-4d73-95ae-ba43dbd46b47"
        setup(bypass = false)

        coEvery { service.getUserByEmail("example@email.com") } returns UserData(
            userId = uuid,
            userName = "oldTestUser",
            password = "testpassword".encrypt(),
            email = "example@email.com"
        )

        val response = client.get("/api/login") {
            setupRequest()
            setBody(
                """
                {
                    "email": "example@email.com",
                    "password": "testpassword"
                }
                """.trimIndent()
            )
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    @Test
    fun `Loggin out correctly, should return OK`() = testApplication {
        setup()

        val response = client.get("/api/logout") {
            setupRequest()
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
    }

    private fun ApplicationTestBuilder.setup(
        bypass: Boolean = true,
        userId: String = ""
    ) {
        setupApplication {
            configureSession(
                bypass,
                userId
            )
            install(Koin) {
                slf4jLogger()
                modules(userModule)
            }
            userRoutes()
        }
    }
    private val userModule = module {
        single<TokenConfig> {
            TokenConfig(
                issuer = "http://0.0.0.0:8080",
                audience = "users",
                expiresIn = 365L * 1000L * 60L * 60L * 24L,
                secret = "secret"
            )
        }
        single<UserRepository> {
            UserRepositoryImpl(service)
        }
        single {
            UserUseCases(
                deleteUserUseCase = DeleteUserUseCase(get()),
                getUserByIdUseCase = GetUserByIdUseCase(get()),
                loginUseCase = LoginUseCase(get(), get()),
                signUpUseCase = SignUpUseCase(get()),
                updatePasswordUseCase = UpdatePasswordUseCase(get()),
                updateUserUseCase = UpdateUserUseCase(get()),
            )
        }
    }
}
