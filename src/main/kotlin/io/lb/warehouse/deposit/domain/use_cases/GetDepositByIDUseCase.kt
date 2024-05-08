package io.lb.warehouse.deposit.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.deposit.data.model.DepositData
import io.lb.warehouse.deposit.domain.repository.DepositRepository

class GetDepositByIDUseCase(
    private val repository: DepositRepository
) {
    suspend operator fun invoke(id: String): DepositData {
        return repository.getDepositById(id) ?: run {
            throw WareHouseException(HttpStatusCode.NotFound, "There is no deposits with such ID")
        }
    }
}
