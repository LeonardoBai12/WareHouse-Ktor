package io.lb.warehouse.withdraw.domain.use_cases

import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.domain.repository.WithdrawRepository

class CreateWithdrawUseCase(
    private val repository: WithdrawRepository
) {
    suspend operator fun invoke(request: WithdrawCreateRequest): String {
        return repository.insertWithdraw(request)
    }
}
