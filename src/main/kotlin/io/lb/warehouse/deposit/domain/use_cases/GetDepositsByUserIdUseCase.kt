package io.lb.warehouse.deposit.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.deposit.data.model.DepositData
import io.lb.warehouse.deposit.domain.repository.DepositRepository

class GetDepositsByUserIdUseCase(
    private val repository: DepositRepository
) {
    suspend operator fun invoke(userUUID: String): List<DepositData> {
        val deposits = repository.getDepositsByUserId(userUUID)

        if (deposits.isEmpty()) {
            throw WareHouseException(HttpStatusCode.NotFound, "There is no deposits for such user")
        }

        return deposits
    }
}
