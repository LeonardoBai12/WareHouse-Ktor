package io.lb.warehouse.ware.domain.use_cases

import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.domain.repository.WareRepository

class CreateWareUseCase(
    private val repository: WareRepository
) {
    suspend operator fun invoke(ware: WareCreateRequest): String {
        return repository.insertWare(ware)
    }
}
