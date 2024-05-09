package io.lb.warehouse.deposit.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.deposit.data.model.DepositData
import io.lb.warehouse.deposit.domain.repository.DepositRepository

/**
 * Use case for retrieving deposits associated with a ware by its UUID.
 *
 * @property repository The repository for interacting with deposit data.
 */
class GetDepositsByWareIdUseCase(
    private val repository: DepositRepository
) {
    /**
     * Retrieves deposits associated with a ware by its UUID.
     *
     * @param wareUUID The UUID of the ware.
     * @return A list of deposits associated with the ware.
     * @throws WareHouseException if no deposits are found for the specified ware.
     */
    suspend operator fun invoke(wareUUID: String): List<DepositData> {
        val deposits = repository.getDepositsByWareId(wareUUID)

        if (deposits.isEmpty()) {
            throw WareHouseException(HttpStatusCode.NotFound, "There are no deposits for such ware")
        }

        return deposits
    }
}
