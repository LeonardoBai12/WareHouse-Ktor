package io.lb.warehouse.withdraw.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.withdraw.data.model.WithdrawData
import io.lb.warehouse.withdraw.domain.repository.WithdrawRepository

class GetWithdrawsByWareIdUseCase(
    private val repository: WithdrawRepository
) {
    suspend operator fun invoke(wareUUID: String): List<WithdrawData> {
        val withdraws = repository.getWithdrawsByWareId(wareUUID)

        if (withdraws.isEmpty()) {
            throw WareHouseException(HttpStatusCode.NotFound, "There is no withdraws for such ware")
        }

        return withdraws
    }
}
