package io.lb.warehouse.user.domain.use_cases

import io.lb.warehouse.user.domain.repository.UserRepository
import io.lb.warehouse.user.util.validatePassword

class DeleteUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String, password: String) {
        repository.validatePassword(userId, password)
        repository.deleteUser(userId)
    }
}
