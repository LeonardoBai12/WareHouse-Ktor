package io.lb.warehouse.withdraw.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.withdraw.data.model.WithdrawData
import io.lb.warehouse.withdraw.domain.model.WithdrawParameters
import io.lb.warehouse.withdraw.domain.repository.WithdrawRepository
import io.lb.warehouse.withdraw.util.getOrderedWithdraws

/**
 * Use case for retrieving withdraws associated with a user by their UUID.
 *
 * @property repository The repository for interacting with withdraw data.
 */
class GetWithdrawsUseCase(
    private val repository: WithdrawRepository
) {
    /**
     * Retrieves withdraws associated with a user by their UUID.
     *
     * @param parameters Parameters used for querying withdraw data.
     * @return A list of withdraws associated with the user.
     * @throws WareHouseException if no withdraws are found for the specified filters.
     */
    suspend operator fun invoke(parameters: WithdrawParameters): List<WithdrawData> {
        val withdraws = repository.getWithdraws(parameters)

        if (withdraws.isEmpty()) {
            throw WareHouseException(HttpStatusCode.NotFound, "There are no withdraws for such filters")
        }

        return withdraws.getOrderedWithdraws(
            parameters.sortBy,
            parameters.order
        )
    }
}
