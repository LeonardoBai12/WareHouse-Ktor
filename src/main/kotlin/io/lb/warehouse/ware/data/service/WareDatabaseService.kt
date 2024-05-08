package io.lb.warehouse.ware.data.service

import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.data.model.WareData
import org.jetbrains.annotations.VisibleForTesting

interface WareDatabaseService {
    /**
     * @suppress
     */
    @VisibleForTesting
    companion object {
        const val CREATE_TABLE_WARE = "ware/create_table_ware.sql"
        const val DELETE_WARE = "ware/delete_ware.sql"
        const val INSERT_WARE = "ware/insert_ware.sql"
        const val SELECT_WARE_BY_ID = "ware/select_ware_by_id.sql"
        const val SELECT_WARES_BY_USER_ID = "ware/select_ware_by_user_id.sql"
        const val UPDATE_WARE = "ware/update_ware.sql"
    }

    suspend fun insertWare(ware: WareCreateRequest): String
    suspend fun getWareById(id: String): WareData?
    suspend fun getWaresByUserId(userUUID: String): List<WareData>
    suspend fun updateWare(uuid: String, ware: WareCreateRequest): Int
    suspend fun deleteWare(id: String): Int
}
