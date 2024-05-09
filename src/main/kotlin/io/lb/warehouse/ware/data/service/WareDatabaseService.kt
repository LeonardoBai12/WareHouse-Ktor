package io.lb.warehouse.ware.data.service

import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.data.model.WareData
import org.jetbrains.annotations.VisibleForTesting

/**
 * Service interface for interacting with the ware table in the PostgreSQL database.
 */
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
        const val SELECT_WARES = "ware/select_wares.sql"
        const val UPDATE_WARE = "ware/update_ware.sql"
    }

    /**
     * Inserts a new ware into the database.
     *
     * @param ware The ware data to insert.
     * @return The UUID of the inserted ware.
     */
    suspend fun insertWare(ware: WareCreateRequest): String

    /**
     * Retrieves a ware by its ID from the database.
     *
     * @param id The ID of the ware to retrieve.
     * @return The ware data, or null if not found.
     */
    suspend fun getWareById(id: String): WareData?

    /**
     * Retrieves wares by user ID from the database.
     *
     * @param userIdFilter The ID of the user associated with the ware, or null if not specified.
     * @param nameFilter The name of the ware, or null if not specified.
     * @param brandFilter The brand of the ware, or null if not specified.
     * @return List of wares associated with the user.
     */
    suspend fun getWares(
        nameFilter: String?,
        brandFilter: String?,
        userIdFilter: String?
    ): List<WareData>

    /**
     * Updates a ware in the database.
     *
     * @param uuid The UUID of the ware to update.
     * @param ware The updated ware data.
     */
    suspend fun updateWare(uuid: String, ware: WareCreateRequest): Int

    /**
     * Deletes a ware from the database.
     *
     * @param id The ID of the ware to delete.
     */
    suspend fun deleteWare(id: String): Int
}
