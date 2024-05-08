package io.lb.warehouse.ware.domain.use_cases

import io.lb.warehouse.ware.domain.repository.WareRepository

class GetWareByIdUseCase(
    private val repository: WareRepository
) {
    suspend operator fun invoke() {

    }
}
