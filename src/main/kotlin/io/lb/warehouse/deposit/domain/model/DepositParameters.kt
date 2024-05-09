package io.lb.warehouse.deposit.domain.model

/**
 * Parameters used for querying withdraw data.
 *
 * @property userId The ID of the user associated with the deposits, or null if not specified.
 * @property wareId The ID of the ware associated with the deposits, or null if not specified.
 * @property sortBy The field to sort the deposits by.
 * @property order The order in which to sort the deposits (ascending or descending).
 */
data class DepositParameters(
    val userId: String?,
    val wareId: String?,
    val sortBy: String,
    val order: String,
)
