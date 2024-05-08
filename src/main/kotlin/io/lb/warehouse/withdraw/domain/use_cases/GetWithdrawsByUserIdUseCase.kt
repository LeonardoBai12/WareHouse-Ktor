package io.lb.warehouse.withdraw.domain.use_cases

import io.lb.warehouse.withdraw.domain.repository.WithdrawRepository

class GetWithdrawsByUserIdUseCase(
    private val repository: WithdrawRepository
) {
    suspend operator fun invoke() {

    }
}
