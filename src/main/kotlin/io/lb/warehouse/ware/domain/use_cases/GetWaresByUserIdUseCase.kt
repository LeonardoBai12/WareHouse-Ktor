package io.lb.warehouse.ware.domain.use_cases

import io.lb.warehouse.ware.domain.repository.WareRepository

class GetWaresByUserIdUseCase(
    private val repository: WareRepository
) {
    suspend operator fun invoke() {

    }
}
