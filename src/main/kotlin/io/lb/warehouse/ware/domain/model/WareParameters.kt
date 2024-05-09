package io.lb.warehouse.ware.domain.model

/**
 * Parameters used for querying ware data.
 *
 * @property userId The ID of the user associated with the ware, or null if not specified.
 * @property name The name of the ware, or null if not specified.
 * @property brand The brand of the ware, or null if not specified.
 * @property sortBy The field to sort the wares by.
 * @property order The order in which to sort the wares (ascending or descending).
 */
data class WareParameters(
    val userId: String?,
    val name: String?,
    val brand: String?,
    val sortBy: String,
    val order: String,
)
