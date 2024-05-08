package io.lb.warehouse.withdraw.data.service

import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.data.model.WithdrawData
import org.jetbrains.annotations.VisibleForTesting

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

    suspend fun insertWithdraw(withdraw: WithdrawCreateRequest): String
    suspend fun getWithdrawById(id: String): WithdrawData?
    suspend fun getWithdrawsByUserId(userUUID: String): List<WithdrawData>
    suspend fun getWithdrawsByWareId(wareUUID: String): List<WithdrawData>
}
