package io.lb.warehouse.deposit.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.deposit.data.model.DepositData
import io.lb.warehouse.deposit.domain.repository.DepositRepository

/**
 * Use case for retrieving deposits associated with a user by their UUID.
 *
 * @property repository The repository for interacting with deposit data.
 */
class GetDepositsByUserIdUseCase(
    private val repository: DepositRepository
) {
    /**
     * Retrieves deposits associated with a user by their UUID.
     *
     * @param userUUID The UUID of the user.
     * @return A list of deposits associated with the user.
     * @throws WareHouseException if no deposits are found for the specified user.
     */
    suspend operator fun invoke(userUUID: String): List<DepositData> {
        val deposits = repository.getDepositsByUserId(userUUID)

        if (deposits.isEmpty()) {
            throw WareHouseException(HttpStatusCode.NotFound, "There are no deposits for such user")
        }

        return deposits
    }
}
