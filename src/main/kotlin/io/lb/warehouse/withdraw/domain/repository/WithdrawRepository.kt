package io.lb.warehouse.withdraw.domain.repository

import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.data.model.WithdrawData

/**
 * Repository interface for interacting with withdrawal data.
 */
interface WithdrawRepository {
    /**
     * Inserts a new withdrawal request.
     *
     * @param withdraw The withdrawal request data to insert.
     * @return The UUID of the newly inserted withdrawal request.
     */
    suspend fun insertWithdraw(withdraw: WithdrawCreateRequest): String

    /**
     * Retrieves a withdrawal request by its ID.
     *
     * @param id The ID of the withdrawal request to retrieve.
     * @return The withdrawal request data, or null if not found.
     */
    suspend fun getWithdrawById(id: String): WithdrawData?

    /**
     * Retrieves all withdrawal requests associated with a user by their UUID.
     *
     * @param userUUID The UUID of the user.
     * @return A list of withdrawal requests associated with the user.
     */
    suspend fun getWithdrawsByUserId(userUUID: String): List<WithdrawData>

    /**
     * Retrieves all withdrawal requests associated with a ware by its UUID.
     *
     * @param wareUUID The UUID of the ware.
     * @return A list of withdrawal requests associated with the ware.
     */
    suspend fun getWithdrawsByWareId(wareUUID: String): List<WithdrawData>
}
