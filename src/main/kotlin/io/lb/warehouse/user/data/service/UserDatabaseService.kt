package io.lb.warehouse.user.data.service

import io.lb.warehouse.core.util.loadQueryFromFile
import io.lb.warehouse.user.data.model.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.util.UUID
import org.jetbrains.annotations.VisibleForTesting

class UserDatabaseService(
    private val connection: Connection,
) {
    companion object {
        @VisibleForTesting
        const val CREATE_TABLE_USER_DATA = "user/create_table_user.sql"
        @VisibleForTesting
        const val DELETE_USER = "user/delete_user.sql"
        @VisibleForTesting
        const val INSERT_USER = "user/insert_user.sql"
        @VisibleForTesting
        const val SELECT_USER_BY_EMAIL = "user/select_user_by_email.sql"
        @VisibleForTesting
        const val SELECT_USER_BY_ID = "user/select_user_by_id.sql"
        @VisibleForTesting
        const val UPDATE_USER = "user/update_user.sql"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(loadQueryFromFile(CREATE_TABLE_USER_DATA))
    }

    suspend fun createUser(user: UserData) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(INSERT_USER))) {
            setObject(1, UUID.fromString(user.userId))
            setString(2, user.userName)
            setString(3, user.password)
            setString(4, user.email)
            setString(5, user.profilePictureUrl)
            executeUpdate()
        }
    }

    suspend fun updateUser(user: UserData) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(UPDATE_USER))) {
            setString(1, user.userName)
            setString(2, user.email)
            setString(3, user.profilePictureUrl)
            setObject(4, UUID.fromString(user.userId))
            executeUpdate()
        }
    }

    suspend fun deleteUser(userId: String) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(DELETE_USER))) {
            setObject(1, UUID.fromString(userId))
            executeUpdate()
        }
    }

    suspend fun getUserById(userId: String): UserData? = withContext(Dispatchers.IO) {
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

    suspend fun isEmailAlreadyInUse(email: String): Boolean = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(loadQueryFromFile(SELECT_USER_BY_EMAIL))
        statement.setString(1, email)
        val resultSet = statement.executeQuery()

        return@withContext resultSet.next()
    }
}
