package io.lb.warehouse.user.data.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import io.lb.warehouse.core.util.loadQueryFromFile
import io.lb.warehouse.user.data.model.UserData
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.CREATE_TABLE_USER_DATA
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.DELETE_USER
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.INSERT_USER
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.SELECT_USER_BY_EMAIL
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.SELECT_USER_BY_ID
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.UPDATE_USER
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.util.UUID
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserDatabaseServiceTest {
    private val connection: Connection = mockk()
    private lateinit var service: UserDatabaseService

    @BeforeEach
    fun setUp() {
        val query = loadQueryFromFile(CREATE_TABLE_USER_DATA)
        val statement: Statement = mockk(relaxed = true)

        every { connection.createStatement() } returns statement

        every {
            statement.executeUpdate(query)
        } returns 1

        service = UserDatabaseService(connection)

        verify {
            connection.createStatement()
            statement.executeUpdate(query)
        }
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Validating if email is in use, should return false`() = runTest {
        val queryResult = mockk<ResultSet>()
        val statement: PreparedStatement = mockk(relaxed = true)

        setUpPreparedStatement(statement, queryResult)

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_EMAIL))
        } returns statement
        every { queryResult.next() } returns false

        val result = service.isEmailAlreadyInUse("user@example.com")

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_EMAIL))
            statement.setString(1, "user@example.com")
            statement.executeQuery()
        }

        assertThat(result).isFalse()
    }

    @Test
    fun `Validating if email is in use, should return true`() = runTest {
        val queryResult = mockk<ResultSet>()
        val statement: PreparedStatement = mockk(relaxed = true)

        setUpPreparedStatement(statement, queryResult)

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_EMAIL))
        } returns statement
        every { queryResult.next() } returns true

        val result = service.isEmailAlreadyInUse("user@example.com")

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_EMAIL))
            statement.setString(1, "user@example.com")
            statement.executeQuery()
        }

        assertThat(result).isTrue()
    }

    @Test
    fun `Getting unexistent user by ID, should return null`() = runTest {
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val queryResult = mockk<ResultSet>()
        val statement: PreparedStatement = mockk(relaxed = true)

        setUpPreparedStatement(statement, queryResult)

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_ID))
        } returns statement
        every { queryResult.next() } returns false

        val result = service.getUserById(userId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_ID))
            statement.setObject(1, UUID.fromString(userId))
            statement.executeQuery()
        }

        assertThat(result).isNull()
    }

    @Test
    fun `Getting user by ID, should return the correct user`() = runTest {
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val queryResult = mockk<ResultSet>()
        val statement: PreparedStatement = mockk(relaxed = true)

        setUpPreparedStatement(statement, queryResult)

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_ID))
        } returns statement
        every { queryResult.next() } returns true

        every {
            queryResult.getString("user_id")
        } returns userId
        every {
            queryResult.getString("user_name")
        } returns "userName"
        every {
            queryResult.getString("password")
        } returns "password"
        every {
            queryResult.getString("email")
        } returns "user@example.com"
        every {
            queryResult.getString("profile_picture")
        } returns "picture"


        val result = service.getUserById(userId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_ID))
            statement.setObject(1, UUID.fromString(userId))
            statement.executeQuery()
        }

        assertThat(result).isNotNull()
        assertThat(result?.userId).isEqualTo(userId)
        assertThat(result?.userName).isEqualTo("userName")
        assertThat(result?.email).isEqualTo("user@example.com")
        assertThat(result?.profilePictureUrl).isEqualTo("picture")
        assertThat(result?.password).isEqualTo("password")
    }

    @Test
    fun `Creating user, should run succesfully`() = runTest {
        val user = UserData(
            userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929",
            userName = "userName",
            password = "password",
            email = "user@example.com",
            profilePictureUrl = "picture",
        )
        val queryResult = mockk<ResultSet>()
        val statement: PreparedStatement = mockk(relaxed = true)

        setUpPreparedStatement(statement, queryResult)

        every {
            connection.prepareStatement(loadQueryFromFile(INSERT_USER))
        } returns statement

        service.createUser(user)

        verify {
            connection.prepareStatement(loadQueryFromFile(INSERT_USER))
            statement.setObject(1, UUID.fromString("75ba8951-d1cd-46cb-bde7-39caa35a8929"))
            statement.setString(2, "userName")
            statement.setString(3, "password")
            statement.setString(4, "user@example.com")
            statement.setString(5, "picture")
            statement.executeUpdate()
        }
    }

    @Test
    fun `Updating user, should run succesfully`() = runTest {
        val user = UserData(
            userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929",
            userName = "userName",
            email = "user@example.com",
            profilePictureUrl = "picture",
        )
        val queryResult = mockk<ResultSet>()
        val statement: PreparedStatement = mockk(relaxed = true)

        setUpPreparedStatement(statement, queryResult)

        every {
            connection.prepareStatement(loadQueryFromFile(UPDATE_USER))
        } returns statement

        service.updateUser(user)

        verify {
            connection.prepareStatement(loadQueryFromFile(UPDATE_USER))
            statement.setString(1, "userName")
            statement.setString(2, "user@example.com")
            statement.setString(3, "picture")
            statement.setObject(4, UUID.fromString("75ba8951-d1cd-46cb-bde7-39caa35a8929"))
            statement.executeUpdate()
        }
    }

    @Test
    fun `Deleting user by ID, should run succesfully`() = runTest {
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val queryResult = mockk<ResultSet>()
        val statement: PreparedStatement = mockk(relaxed = true)

        setUpPreparedStatement(statement, queryResult)

        every {
            connection.prepareStatement(loadQueryFromFile(DELETE_USER))
        } returns statement

        service.deleteUser(userId)

        verify {
            connection.prepareStatement(loadQueryFromFile(DELETE_USER))
            statement.setObject(1, UUID.fromString(userId))
            statement.executeUpdate()
        }
    }

    private fun setUpPreparedStatement(statement: PreparedStatement, queryResult: ResultSet) {
        with(statement) {
            every { setObject(any(), any()) } just runs
            every { setString(any(), any()) } just runs
            every { setDouble(any(), any()) } just runs

            every { executeQuery() } returns queryResult
            every { executeUpdate() } returns 1
        }
    }
}
