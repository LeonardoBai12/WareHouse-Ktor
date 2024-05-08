package io.lb.warehouse.user.data.service

import io.lb.warehouse.user.data.model.UserData
import org.jetbrains.annotations.VisibleForTesting

/**
 * Service interface for interacting with the user data table in the PostgreSQL database.
 */
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

    /**
     * Creates a new user in the database.
     *
     * @param user The user data to insert.
     */
    suspend fun createUser(user: UserData): Int

    /**
     * Updates an existing user in the database.
     *
     * @param user The updated user data.
     */
    suspend fun updateUser(user: UserData): Int

    /**
     * Updates the password of a user in the database.
     *
     * @param userId The ID of the user whose password to update.
     * @param newPassword The new password.
     */
    suspend fun updatePassword(userId: String, newPassword: String): Int

    /**
     * Deletes a user from the database.
     *
     * @param userId The ID of the user to delete.
     */
    suspend fun deleteUser(userId: String): Int

    /**
     * Retrieves a user by ID from the database.
     *
     * @param userId The ID of the user to retrieve.
     * @return The user data, or null if not found.
     */
    suspend fun getUserById(userId: String): UserData?

    /**
     * Checks if an email is already in use in the database.
     *
     * @param email The email to check.
     * @return True if the email is already in use, false otherwise.
     */
    suspend fun isEmailAlreadyInUse(email: String): Boolean
}
