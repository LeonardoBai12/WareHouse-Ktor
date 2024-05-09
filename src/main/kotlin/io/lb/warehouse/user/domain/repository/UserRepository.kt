package io.lb.warehouse.user.domain.repository

import io.lb.warehouse.user.data.model.UserData

/**
 * Repository interface for interacting with user data.
 */
interface UserRepository {
    /**
     * Creates a new user.
     *
     * @param user The user data to insert.
     * @return The ID of the newly created user.
     */
    suspend fun createUser(user: UserData): Int

    /**
     * Updates an existing user.
     *
     * @param user The updated user data.
     * @return The number of rows affected by the update operation.
     */
    suspend fun updateUser(user: UserData): Int

    /**
     * Updates the password of a user.
     *
     * @param userId The ID of the user whose password to update.
     * @param newPassword The new password.
     * @return The number of rows affected by the update operation.
     */
    suspend fun updatePassword(userId: String, newPassword: String): Int

    /**
     * Deletes a user.
     *
     * @param userId The ID of the user to delete.
     * @return The number of rows affected by the delete operation.
     */
    suspend fun deleteUser(userId: String): Int

    /**
     * Retrieves a user by ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The user data, or null if not found.
     */
    suspend fun getUserById(userId: String): UserData?

    /**
     * Retrieves a user by email.
     *
     * @param email The email of the user to retrieve.
     * @return The user data, or null if not found.
     */
    suspend fun getUserByEmail(email: String): UserData?

    /**
     * Checks if an email is already in use.
     *
     * @param email The email to check.
     * @return True if the email is already in use, false otherwise.
     */
    suspend fun isEmailAlreadyInUse(email: String): Boolean
}
