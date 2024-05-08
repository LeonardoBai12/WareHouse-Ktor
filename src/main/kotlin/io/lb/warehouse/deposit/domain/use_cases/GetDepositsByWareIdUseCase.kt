package io.lb.warehouse.deposit.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.deposit.data.model.DepositData
import io.lb.warehouse.deposit.domain.repository.DepositRepository

class GetDepositsByWareIdUseCase(
    private val repository: DepositRepository
) {
    suspend operator fun invoke(wareUUID: String): List<DepositData> {
        val deposits = repository.getDepositsByWareId(wareUUID)

        if (deposits.isEmpty()) {
            throw WareHouseException(HttpStatusCode.NotFound, "There is no deposits for such ware")
        }

        return deposits
    }
}
