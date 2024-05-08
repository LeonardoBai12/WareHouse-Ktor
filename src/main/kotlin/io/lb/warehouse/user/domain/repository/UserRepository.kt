package io.lb.warehouse.user.domain.repository

import io.lb.warehouse.user.data.model.UserData

interface UserRepository {
    suspend fun createUser(user: UserData): Int
    suspend fun updateUser(user: UserData): Int
    suspend fun updatePassword(userId: String, newPassword: String): Int
    suspend fun deleteUser(userId: String): Int
    suspend fun getUserById(userId: String): UserData?
    suspend fun isEmailAlreadyInUse(email: String): Boolean
}
