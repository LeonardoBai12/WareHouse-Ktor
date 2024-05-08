package io.lb.warehouse.deposit.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.deposit.data.model.DepositData
import io.lb.warehouse.deposit.domain.repository.DepositRepository

/**
 * Use case for retrieving a deposit by its ID.
 *
 * @property repository The repository for interacting with deposit data.
 */
class GetDepositByIDUseCase(
    private val repository: DepositRepository
) {
    /**
     * Retrieves a deposit by its ID.
     *
     * @param id The ID of the deposit to retrieve.
     * @return The deposit data if found.
     * @throws WareHouseException if no deposit is found with the specified ID.
     */
    suspend operator fun invoke(id: String): DepositData {
        return repository.getDepositById(id) ?: run {
            throw WareHouseException(HttpStatusCode.NotFound, "There is no deposit with such ID")
        }
    }
}

