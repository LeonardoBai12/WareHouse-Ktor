package io.lb.warehouse.deposit.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.deposit.data.model.DepositData
import io.lb.warehouse.deposit.domain.model.DepositParameters
import io.lb.warehouse.deposit.domain.repository.DepositRepository
import io.lb.warehouse.deposit.util.getOrderedDeposits

/**
 * Use case for retrieving deposits associated with a user by their UUID.
 *
 * @property repository The repository for interacting with deposit data.
 */
class GetDepositsUseCase(
    private val repository: DepositRepository
) {
    /**
     * Retrieves deposits associated with a user by their UUID.
     *
     * @param parameters Parameters used for querying deposit data.
     * @return A list of deposits associated with the user.
     * @throws WareHouseException if no deposits are found for the specified user.
     */
    suspend operator fun invoke(parameters: DepositParameters): List<DepositData> {
        val deposits = repository.getDeposits(parameters)

        if (deposits.isEmpty()) {
            throw WareHouseException(
                HttpStatusCode.NotFound,
                "There are no deposits for such filters"
            )
        }

        return deposits.getOrderedDeposits(
            parameters.sortBy,
            parameters.order
        )
    }
}
