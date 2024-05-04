package io.lb.warehouse.withdraw.data.model

import kotlinx.serialization.Serializable

/**
 * Data class representing withdrawal data.
 *
 * @property uuid The unique identifier for the withdrawal.
 * @property userId The ID of the user who performed the withdrawal.
 * @property wareId The ID of the ware that was withdrawn.
 * @property quantity The quantity of ware that was withdrawn.
 */
@Serializable
data class WithdrawData(
    val uuid: String,
    val userId: String,
    val wareId: String,
    val quantity: Double,
)
