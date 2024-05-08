package io.lb.warehouse.deposit.domain.use_cases

import io.lb.warehouse.deposit.domain.repository.DepositRepository

class GetDepositsByWareIdUseCase(
    private val repository: DepositRepository
) {
    suspend operator fun invoke() {

    }
}
