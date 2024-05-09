package io.lb.warehouse.withdraw.domain.repository

import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.data.model.WithdrawData
import io.lb.warehouse.withdraw.domain.model.WithdrawParameters

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
     * @param parameters Parameters used for querying withdraw data.
     * @return A list of withdrawal requests associated with the user.
     */
    suspend fun getWithdraws(parameters: WithdrawParameters): List<WithdrawData>
}
