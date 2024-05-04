package io.lb.warehouse.withdraw.data.model

import kotlinx.serialization.Serializable

/**
 * Data class representing a request to withdraw an amount of ware.
 *
 * @property userId The ID of the user performing the withdrawal.
 * @property wareId The ID of the ware being withdrawn.
 * @property quantity The quantity of ware to withdraw.
 */
@Serializable
data class WithdrawCreateRequest(
    val userId: String,
    val wareId: String,
    val quantity: Double
)
