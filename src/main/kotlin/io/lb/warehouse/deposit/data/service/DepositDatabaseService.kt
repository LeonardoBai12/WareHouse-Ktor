package io.lb.warehouse.deposit.data.service

import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.data.model.DepositData
import org.jetbrains.annotations.VisibleForTesting

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
        const val SELECT_DEPOSITS_BY_USER_ID = "deposit/select_deposit_by_user_id.sql"
        const val SELECT_DEPOSITS_BY_WARE_ID = "deposit/select_deposit_by_ware_id.sql"
    }

    suspend fun insertDeposit(deposit: DepositCreateRequest): String
    suspend fun getDepositById(id: String): DepositData?
    suspend fun getDepositsByUserId(userUUID: String): List<DepositData>
    suspend fun getDepositsByWareId(wareUUID: String): List<DepositData>
}
