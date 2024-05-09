package io.lb.warehouse.user.data.service

import io.lb.warehouse.core.util.loadQueryFromFile
import io.lb.warehouse.user.data.model.UserData
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.CREATE_TABLE_USER_DATA
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.DELETE_USER
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.INSERT_USER
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.SELECT_USER_BY_EMAIL
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.SELECT_USER_BY_ID
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.UPDATE_PASSWORD
import io.lb.warehouse.user.data.service.UserDatabaseService.Companion.UPDATE_USER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.util.UUID

/**
 * Service class for interacting with the user data table in the PostgreSQL database.
 *
 * @property connection Connection to the database.
 */
class UserDatabaseServiceImpl(private val connection: Connection) : UserDatabaseService {
    init {
        val statement = connection.createStatement()
        statement.executeUpdate(loadQueryFromFile(CREATE_TABLE_USER_DATA))
    }

    override suspend fun createUser(user: UserData) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(INSERT_USER))) {
            setObject(1, UUID.fromString(user.userId))
            setString(2, user.userName)
            setString(3, user.password)
            setString(4, user.email)
            setString(5, user.profilePictureUrl)
            executeUpdate()
        }
    }

    override suspend fun updateUser(user: UserData) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(UPDATE_USER))) {
            setString(1, user.userName)
            setString(2, user.email)
            setString(3, user.profilePictureUrl)
            setObject(4, UUID.fromString(user.userId))
            executeUpdate()
        }
    }

    override suspend fun updatePassword(userId: String, newPassword: String) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(UPDATE_PASSWORD))) {
            setString(1, newPassword)
            setObject(2, UUID.fromString(userId))
            executeUpdate()
        }
    }

    override suspend fun deleteUser(userId: String) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(DELETE_USER))) {
            setObject(1, UUID.fromString(userId))
            executeUpdate()
        }
    }

    override suspend fun getUserById(userId: String): UserData? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_ID))
        statement.setObject(1, UUID.fromString(userId))
        val resultSet = statement.executeQuery()

        return@withContext if (resultSet.next()) {
            UserData(
                userId = resultSet.getString("user_id"),
                userName = resultSet.getString("user_name"),
                password = resultSet.getString("password"),
                email = resultSet.getString("email"),
                profilePictureUrl = resultSet.getString("profile_picture"),
            )
        } else {
            null
        }
    }

    override suspend fun getUserByEmail(email: String): UserData? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_EMAIL))
        statement.setObject(1, UUID.fromString(email))
        val resultSet = statement.executeQuery()

        return@withContext if (resultSet.next()) {
            UserData(
                userId = resultSet.getString("user_id"),
                userName = resultSet.getString("user_name"),
                password = resultSet.getString("password"),
                email = resultSet.getString("email"),
                profilePictureUrl = resultSet.getString("profile_picture"),
            )
        } else {
            null
        }
    }

    override suspend fun isEmailAlreadyInUse(email: String): Boolean = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_EMAIL))
        statement.setString(1, email)
        val resultSet = statement.executeQuery()

        return@withContext resultSet.next()
    }
}
