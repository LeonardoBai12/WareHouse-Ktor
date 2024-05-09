package io.lb.warehouse.withdraw.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.withdraw.data.model.WithdrawData
import io.lb.warehouse.withdraw.domain.repository.WithdrawRepository

/**
 * Use case for retrieving withdraws associated with a ware by its UUID.
 *
 * @property repository The repository for interacting with withdraw data.
 */
class GetWithdrawsByWareIdUseCase(
    private val repository: WithdrawRepository
) {
    /**
     * Retrieves withdraws associated with a ware by its UUID.
     *
     * @param wareUUID The UUID of the ware.
     * @return A list of withdraws associated with the ware.
     * @throws WareHouseException if no withdraws are found for the specified ware.
     */
    suspend operator fun invoke(wareUUID: String): List<WithdrawData> {
        val withdraws = repository.getWithdrawsByWareId(wareUUID)

        if (withdraws.isEmpty()) {
            throw WareHouseException(HttpStatusCode.NotFound, "There are no withdraws for such ware")
        }

        return withdraws
    }
}
