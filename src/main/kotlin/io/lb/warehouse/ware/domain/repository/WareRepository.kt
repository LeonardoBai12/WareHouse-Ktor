package io.lb.warehouse.ware.domain.repository

import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.data.model.WareData

/**
 * Repository interface for interacting with ware data.
 */
interface WareRepository {
    /**
     * Inserts a new ware.
     *
     * @param ware The ware data to insert.
     * @return The UUID of the newly inserted ware.
     */
    suspend fun insertWare(ware: WareCreateRequest): String

    /**
     * Retrieves a ware by its ID.
     *
     * @param id The ID of the ware to retrieve.
     * @return The ware data, or null if not found.
     */
    suspend fun getWareById(id: String): WareData?

    /**
     * Retrieves all wares associated with a user by their UUID.
     *
     * @param userUUID The UUID of the user.
     * @return A list of wares associated with the user.
     */
    suspend fun getWaresByUserId(userUUID: String): List<WareData>

    /**
     * Updates an existing ware.
     *
     * @param uuid The UUID of the ware to update.
     * @param ware The updated ware data.
     * @return The number of rows affected by the update operation.
     */
    suspend fun updateWare(uuid: String, ware: WareCreateRequest): Int

    /**
     * Deletes a ware by its ID.
     *
     * @param id The ID of the ware to delete.
     * @return The number of rows affected by the delete operation.
     */
    suspend fun deleteWare(id: String): Int
}
