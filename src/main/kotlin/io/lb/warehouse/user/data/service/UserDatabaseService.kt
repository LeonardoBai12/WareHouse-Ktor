package io.lb.warehouse.user.data.service

import io.lb.warehouse.user.data.model.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.util.UUID

class UserDatabaseService(
    private val connection: Connection,
) {
    companion object {
        private const val CREATE_TABLE_USER_DATA =
            "CREATE TABLE IF NOT EXISTS user_data ( " +
                "     user_id UUID PRIMARY KEY, " +
                "     user_name VARCHAR(255) NOT NULL, " +
                "     password VARCHAR(255) NOT NULL, " +
                "     profile_picture VARCHAR(255), " +
                "     email VARCHAR(255) UNIQUE NOT NULL " +
                ");"
        private const val SELECT_USER_BY_ID =
            "SELECT user_id, user_name, password, email, profile_picture FROM user_data WHERE user_id = ?;"
        private const val SELECT_USER_BY_EMAIL =
            "SELECT user_id, user_name, password, email, profile_picture FROM user_data WHERE email = ?;"
        private const val INSERT_USER =
            "INSERT INTO user_data (user_id, user_name, password, email, profile_picture) VALUES (?, ?, ?, ?, ?);"
        private const val UPDATE_USER =
            "UPDATE user_data SET user_name = ?, email = ?, profile_picture = ? WHERE user_id = ?;"
        private const val DELETE_USER = "DELETE FROM user_data WHERE user_id = ?;"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_USER_DATA)
    }

    suspend fun createUser(user: UserData) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(INSERT_USER)) {
            setObject(1, UUID.fromString(user.userId))
            setString(2, user.userName)
            setString(3, user.password)
            setString(4, user.email)
            setString(5, user.profilePictureUrl)
            executeUpdate()
        }
    }

    suspend fun updateUser(user: UserData) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(UPDATE_USER)) {
            setString(1, user.userName)
            setString(2, user.email)
            setString(3, user.profilePictureUrl)
            setObject(4, UUID.fromString(user.userId))
            executeUpdate()
        }
    }

    suspend fun deleteUser(userId: String) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(DELETE_USER)) {
            setObject(1, UUID.fromString(userId))
            executeUpdate()
        }
    }

    suspend fun getUserById(userId: String): UserData? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_USER_BY_ID)
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

    suspend fun isEmailAlreadyInUse(email: String): Boolean = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_USER_BY_EMAIL)
        statement.setString(1, email)
        val resultSet = statement.executeQuery()

        return@withContext resultSet.next()
    }
}
