package io.lb.warehouse.withdraw.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.withdraw.data.model.WithdrawData
import io.lb.warehouse.withdraw.domain.repository.WithdrawRepository

/**
 * Use case for retrieving withdraws associated with a user by their UUID.
 *
 * @property repository The repository for interacting with withdraw data.
 */
class GetWithdrawsByUserIdUseCase(
    private val repository: WithdrawRepository
) {
    /**
     * Retrieves withdraws associated with a user by their UUID.
     *
     * @param userUUID The UUID of the user.
     * @return A list of withdraws associated with the user.
     * @throws WareHouseException if no withdraws are found for the specified user.
     */
    suspend operator fun invoke(userUUID: String): List<WithdrawData> {
        val withdraws = repository.getWithdrawsByUserId(userUUID)

        if (withdraws.isEmpty()) {
            throw WareHouseException(HttpStatusCode.NotFound, "There are no withdraws for such user")
        }

        return withdraws
    }
}
