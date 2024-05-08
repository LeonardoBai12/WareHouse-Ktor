package io.lb.warehouse.withdraw.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.withdraw.data.model.WithdrawData
import io.lb.warehouse.withdraw.domain.repository.WithdrawRepository

class GetWithdrawByIDUseCase(
    private val repository: WithdrawRepository
) {
    suspend operator fun invoke(id: String): WithdrawData {
        return repository.getWithdrawById(id) ?: run {
            throw WareHouseException(HttpStatusCode.NotFound, "There is no withdraws with such ID")
        }
    }
}
