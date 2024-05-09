package io.lb.warehouse.withdraw.data.service

import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.data.model.WithdrawData
import org.jetbrains.annotations.VisibleForTesting

/**
 * Service interface for interacting with the withdrawal table in the PostgreSQL database.
 */
interface WithdrawDatabaseService {
    /**
     * @suppress
     */
    @VisibleForTesting
    companion object {
        const val CREATE_AFTER_WITHDRAW_TRIGGER = "withdraw/create_after_withdraw_trigger.sql"
        const val CREATE_UPDATE_QUANTITY_FUNCTION =
            "withdraw/create_update_available_quantity_function_on_withdraw.sql"
        const val CREATE_TABLE_WITHDRAW = "withdraw/create_table_withdraw.sql"
        const val INSERT_WITHDRAW = "withdraw/insert_withdraw.sql"
        const val SELECT_WITHDRAW_BY_ID = "withdraw/select_withdraw_by_id.sql"
        const val SELECT_WITHDRAWS_BY_USER_ID = "withdraw/select_withdraw_by_user_id.sql"
        const val SELECT_WITHDRAWS_BY_WARE_ID = "withdraw/select_withdraw_by_ware_id.sql"
    }

    /**
     * Inserts a withdrawal into the database.
     *
     * @param withdraw The withdrawal data to insert.
     * @return The UUID of the inserted withdrawal.
     */
    suspend fun insertWithdraw(withdraw: WithdrawCreateRequest): String

    /**
     * Retrieves a withdrawals by its ID from the database.
     *
     * @param id The ID of the withdrawals to retrieve.
     * @return The withdrawals data, or null if not found.
     */
    suspend fun getWithdrawById(id: String): WithdrawData?

    /**
     * Retrieves withdrawals by user ID from the database.
     *
     * @param userUUID The UUID of the user.
     * @return List of withdrawals associated with the user.
     */
    suspend fun getWithdrawsByUserId(userUUID: String): List<WithdrawData>

    /**
     * Retrieves withdrawals by ware ID from the database.
     *
     * @param wareUUID The UUID of the ware.
     * @return List of withdrawals associated with the ware.
     */
    suspend fun getWithdrawsByWareId(wareUUID: String): List<WithdrawData>
}
