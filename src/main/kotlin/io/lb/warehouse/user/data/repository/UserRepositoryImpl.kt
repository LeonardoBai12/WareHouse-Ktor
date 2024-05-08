package io.lb.warehouse.user.data.repository

import io.lb.warehouse.user.data.model.UserData
import io.lb.warehouse.user.data.service.UserDatabaseService
import io.lb.warehouse.user.domain.repository.UserRepository

class UserRepositoryImpl(
    private val service: UserDatabaseService
) : UserRepository {
    override suspend fun createUser(user: UserData): Int {
        return service.createUser(user)
    }

    override suspend fun updateUser(user: UserData): Int {
        return service.updateUser(user)
    }

    override suspend fun updatePassword(userId: String, newPassword: String): Int {
        return service.updatePassword(userId, newPassword)
    }

    override suspend fun deleteUser(userId: String): Int {
        return service.deleteUser(userId)
    }

    override suspend fun getUserById(userId: String): UserData? {
        return service.getUserById(userId)
    }

    override suspend fun isEmailAlreadyInUse(email: String): Boolean {
        return service.isEmailAlreadyInUse(email)
    }
}
