package io.lb.warehouse.deposit.data.service

import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.data.model.DepositData
import org.jetbrains.annotations.VisibleForTesting

/**
 * Service interface for interacting with the deposit table in the PostgreSQL database.
 */
interface DepositDatabaseService {
    /**
     * @suppress
     */
    @VisibleForTesting
    companion object {
        const val CREATE_AFTER_DEPOSIT_TRIGGER = "deposit/create_after_deposit_trigger.sql"
        const val CREATE_UPDATE_QUANTITY_FUNCTION =
            "deposit/create_update_available_quantity_function_on_deposit.sql"
        const val CREATE_TABLE_DEPOSIT = "deposit/create_table_deposit.sql"
        const val INSERT_DEPOSIT = "deposit/insert_deposit.sql"
        const val SELECT_DEPOSIT_BY_ID = "deposit/select_deposit_by_id.sql"
        const val SELECT_DEPOSITS = "deposit/select_deposits.sql"
    }

    /**
     * Inserts a deposit into the database.
     *
     * @param deposit The deposit data to insert.
     * @return The UUID of the inserted deposit.
     */
    suspend fun insertDeposit(deposit: DepositCreateRequest): String

    /**
     * Retrieves a deposit by its ID from the database.
     *
     * @param id The ID of the deposit to retrieve.
     * @return The deposit data, or null if not found.
     */
    suspend fun getDepositById(id: String): DepositData?

    /**
     * Retrieves deposits by user ID from the database.
     *
     * @param userUUID The UUID of the user.
     * @param wareUUID The UUID of the ware.
     * @return List of deposits associated with the user.
     */
    suspend fun getDeposits(userUUID: String?, wareUUID: String?): List<DepositData>
}
