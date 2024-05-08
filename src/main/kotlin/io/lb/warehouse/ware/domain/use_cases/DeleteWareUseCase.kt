package io.lb.warehouse.ware.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.ware.domain.repository.WareRepository

class DeleteWareUseCase(
    private val repository: WareRepository
) {
    suspend operator fun invoke(id: String) {
        repository.getWareById(id) ?: run {
            throw WareHouseException(HttpStatusCode.NotFound, "There is no wares with such ID")
        }

        repository.deleteWare(id)
    }
}
