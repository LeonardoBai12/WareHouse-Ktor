package io.lb.warehouse.deposit.domain.use_cases

import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.domain.repository.DepositRepository

class CreateDepositUseCase(
    private val repository: DepositRepository
) {
    suspend operator fun invoke(request: DepositCreateRequest): String {
        return repository.insertDeposit(request)
    }
}