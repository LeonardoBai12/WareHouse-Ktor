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

    /**
     * Creates a new user in the database.
     *
     * @param user The user data to insert.
     */
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

    /**
     * Updates an existing user in the database.
     *
     * @param user The updated user data.
     */
    override suspend fun updateUser(user: UserData) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(UPDATE_USER))) {
            setString(1, user.userName)
            setString(2, user.email)
            setString(3, user.profilePictureUrl)
            setObject(4, UUID.fromString(user.userId))
            executeUpdate()
        }
    }

    /**
     * Updates the password of a user in the database.
     *
     * @param userId The ID of the user whose password to update.
     * @param newPassword The new password.
     */
    override suspend fun updatePassword(userId: String, newPassword: String) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(UPDATE_PASSWORD))) {
            setString(1, newPassword)
            setObject(2, UUID.fromString(userId))
            executeUpdate()
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param userId The ID of the user to delete.
     */
    override suspend fun deleteUser(userId: String) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(DELETE_USER))) {
            setObject(1, UUID.fromString(userId))
            executeUpdate()
        }
    }

    /**
     * Retrieves a user by ID from the database.
     *
     * @param userId The ID of the user to retrieve.
     * @return The user data, or null if not found.
     */
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

    /**
     * Checks if an email is already in use in the database.
     *
     * @param email The email to check.
     * @return True if the email is already in use, false otherwise.
     */
    override suspend fun isEmailAlreadyInUse(email: String): Boolean = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_EMAIL))
        statement.setString(1, email)
        val resultSet = statement.executeQuery()

        return@withContext resultSet.next()
    }
}
