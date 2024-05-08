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
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.UPDATE_PASSWORD
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.UPDATE_USER
import io.lb.warehouse.util.BaseServiceTest
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class UserDatabaseServiceImplTest : BaseServiceTest(CREATE_TABLE_USER_DATA) {
    private lateinit var service: UserDatabaseService

    @BeforeEach
    override fun setUp() {
        super.setUp()
        service = UserDatabaseServiceImpl(connection)
    }

    @Test
    fun `Instantiating service, should call create table`() {
        verify {
            connection.createStatement()
            statement.executeUpdate(loadQueryFromFile(CREATE_TABLE_USER_DATA))
        }
    }

    @Test
    fun `Validating if email is in use, should return false`() = runTest {
        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_EMAIL))
        } returns preparedStatement
        every { queryResult.next() } returns false

        val result = service.isEmailAlreadyInUse("user@example.com")

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_EMAIL))
            preparedStatement.setString(1, "user@example.com")
            preparedStatement.executeQuery()
        }

        assertThat(result).isFalse()
    }

    @Test
    fun `Validating if email is in use, should return true`() = runTest {
        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_EMAIL))
        } returns preparedStatement
        every { queryResult.next() } returns true

        val result = service.isEmailAlreadyInUse("user@example.com")

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_EMAIL))
            preparedStatement.setString(1, "user@example.com")
            preparedStatement.executeQuery()
        }

        assertThat(result).isTrue()
    }

    @Test
    fun `Getting unexistent user by ID, should return null`() = runTest {
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_ID))
        } returns preparedStatement
        every { queryResult.next() } returns false

        val result = service.getUserById(userId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_ID))
            preparedStatement.setObject(1, UUID.fromString(userId))
            preparedStatement.executeQuery()
        }

        assertThat(result).isNull()
    }

    @Test
    fun `Getting user by ID, should return the correct user`() = runTest {
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_ID))
        } returns preparedStatement
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
            preparedStatement.setObject(1, UUID.fromString(userId))
            preparedStatement.executeQuery()
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

        every {
            connection.prepareStatement(loadQueryFromFile(INSERT_USER))
        } returns preparedStatement

        service.createUser(user)

        verify {
            connection.prepareStatement(loadQueryFromFile(INSERT_USER))
            preparedStatement.setObject(1, UUID.fromString("75ba8951-d1cd-46cb-bde7-39caa35a8929"))
            preparedStatement.setString(2, "userName")
            preparedStatement.setString(3, "password")
            preparedStatement.setString(4, "user@example.com")
            preparedStatement.setString(5, "picture")
            preparedStatement.executeUpdate()
        }
    }

    @Test
    fun `Updating user, should run succesfully`() = runTest {
        val uuid = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val user = UserData(
            userId = uuid,
            userName = "userName",
            email = "user@example.com",
            profilePictureUrl = "picture",
        )

        every {
            connection.prepareStatement(loadQueryFromFile(UPDATE_USER))
        } returns preparedStatement

        service.updateUser(user)

        verify {
            connection.prepareStatement(loadQueryFromFile(UPDATE_USER))
            preparedStatement.setString(1, "userName")
            preparedStatement.setString(2, "user@example.com")
            preparedStatement.setString(3, "picture")
            preparedStatement.setObject(4, UUID.fromString(uuid))
            preparedStatement.executeUpdate()
        }
    }

    @Test
    fun `Updating password, should run succesfully`() = runTest {
        val uuid = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        every {
            connection.prepareStatement(loadQueryFromFile(UPDATE_PASSWORD))
        } returns preparedStatement

        service.updatePassword(uuid, "newPassword")

        verify {
            connection.prepareStatement(loadQueryFromFile(UPDATE_PASSWORD))
            preparedStatement.setString(1, "newPassword")
            preparedStatement.setObject(2, UUID.fromString(uuid))
            preparedStatement.executeUpdate()
        }
    }

    @Test
    fun `Deleting user by ID, should run succesfully`() = runTest {
        val userId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        every {
            connection.prepareStatement(loadQueryFromFile(DELETE_USER))
        } returns preparedStatement

        service.deleteUser(userId)

        verify {
            connection.prepareStatement(loadQueryFromFile(DELETE_USER))
            preparedStatement.setObject(1, UUID.fromString(userId))
            preparedStatement.executeUpdate()
        }
    }
}
