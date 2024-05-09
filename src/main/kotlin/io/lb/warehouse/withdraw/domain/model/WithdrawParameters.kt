package io.lb.warehouse.withdraw.domain.model

/**
 * Parameters used for querying withdraw data.
 *
 * @property userId The ID of the user associated with the withdrawals, or null if not specified.
 * @property wareId The ID of the ware associated with the withdrawals, or null if not specified.
 * @property sortBy The field to sort the withdrawal by.
 * @property order The order in which to sort the withdrawals (ascending or descending).
 */
data class WithdrawParameters(
    val userId: String?,
    val wareId: String?,
    val sortBy: String,
    val order: String,
)
