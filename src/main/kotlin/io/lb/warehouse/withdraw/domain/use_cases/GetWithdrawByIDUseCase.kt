package io.lb.warehouse.withdraw.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.withdraw.data.model.WithdrawData
import io.lb.warehouse.withdraw.domain.repository.WithdrawRepository

/**
 * Use case for retrieving a withdrawal by its ID.
 *
 * @property repository The repository for interacting with withdraw data.
 */
class GetWithdrawByIDUseCase(
    private val repository: WithdrawRepository
) {
    /**
     * Retrieves a withdrawal by its ID.
     *
     * @param id The ID of the withdrawal to retrieve.
     * @return The withdrawal data if found.
     * @throws WareHouseException if no withdraw is found with the specified ID.
     */
    suspend operator fun invoke(id: String): WithdrawData {
        return repository.getWithdrawById(id) ?: run {
            throw WareHouseException(HttpStatusCode.NotFound, "There is no withdraw with such ID")
        }
    }
}
