package io.lb.warehouse.user.data.service

import io.lb.warehouse.user.data.model.UserData
import org.jetbrains.annotations.VisibleForTesting

interface UserDatabaseService {
    /**
     * @suppress
     */
    @VisibleForTesting
    companion object {
        const val CREATE_TABLE_USER_DATA = "user/create_table_user.sql"
        const val DELETE_USER = "user/delete_user.sql"
        const val INSERT_USER = "user/insert_user.sql"
        const val SELECT_USER_BY_EMAIL = "user/select_user_by_email.sql"
        const val SELECT_USER_BY_ID = "user/select_user_by_id.sql"
        const val UPDATE_PASSWORD = "user/update_password.sql"
        const val UPDATE_USER = "user/update_user.sql"
    }

    suspend fun createUser(user: UserData): Int
    suspend fun updateUser(user: UserData): Int
    suspend fun updatePassword(userId: String, newPassword: String): Int
    suspend fun deleteUser(userId: String): Int
    suspend fun getUserById(userId: String): UserData?
    suspend fun isEmailAlreadyInUse(email: String): Boolean
}
