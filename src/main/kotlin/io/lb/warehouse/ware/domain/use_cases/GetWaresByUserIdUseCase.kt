package io.lb.warehouse.ware.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.ware.data.model.WareData
import io.lb.warehouse.ware.domain.repository.WareRepository
import io.lb.warehouse.ware.util.getOrderedWares

class GetWaresByUserIdUseCase(
    private val repository: WareRepository
) {
    suspend operator fun invoke(userId: String, sortBy: String, order: String): List<WareData> {
        val result = repository.getWaresByUserId(userId)

        if (result.isEmpty()) {
            throw WareHouseException(HttpStatusCode.NotFound, "There is no wares for such user")
        }

        return result.getOrderedWares(sortBy, order)
    }
}
