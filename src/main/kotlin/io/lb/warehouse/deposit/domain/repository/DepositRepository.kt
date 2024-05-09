package io.lb.warehouse.deposit.domain.repository

import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.data.model.DepositData
import io.lb.warehouse.deposit.domain.model.DepositParameters

/**
 * Repository interface for interacting with deposit data.
 */
interface DepositRepository {
    /**
     * Inserts a new deposit.
     *
     * @param deposit The deposit data to insert.
     * @return The UUID of the newly inserted deposit.
     */
    suspend fun insertDeposit(deposit: DepositCreateRequest): String

    /**
     * Retrieves a deposit by its ID.
     *
     * @param id The ID of the deposit to retrieve.
     * @return The deposit data, or null if not found.
     */
    suspend fun getDepositById(id: String): DepositData?

    /**
     * Retrieves all deposits associated with a user by their UUID.
     *
     * @param parameters Parameters used for querying deposit data.
     * @return A list of deposits associated with the filters.
     */
    suspend fun getDeposits(parameters: DepositParameters): List<DepositData>
}
