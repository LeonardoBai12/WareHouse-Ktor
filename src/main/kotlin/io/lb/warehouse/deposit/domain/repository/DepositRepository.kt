package io.lb.warehouse.deposit.domain.repository

import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.data.model.DepositData

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
     * @param userUUID The UUID of the user.
     * @return A list of deposits associated with the user.
     */
    suspend fun getDepositsByUserId(userUUID: String): List<DepositData>

    /**
     * Retrieves all deposits associated with a ware by its UUID.
     *
     * @param wareUUID The UUID of the ware.
     * @return A list of deposits associated with the ware.
     */
    suspend fun getDepositsByWareId(wareUUID: String): List<DepositData>
}
