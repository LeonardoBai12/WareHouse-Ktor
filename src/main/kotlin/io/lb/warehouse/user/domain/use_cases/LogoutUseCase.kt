package io.lb.warehouse.user.domain.use_cases

import io.lb.warehouse.user.domain.repository.UserRepository

class LogoutUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke() {

    }
}
