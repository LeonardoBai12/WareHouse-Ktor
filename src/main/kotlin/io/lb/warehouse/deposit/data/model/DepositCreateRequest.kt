package io.lb.warehouse.deposit.data.model

import kotlinx.serialization.Serializable

/**
 * Serializable data class representing deposit request data.
 *
 * @property userId User identifier associated with the deposit.
 * @property wareId Identifier for the ware associated with the deposit.
 * @property quantity Quantity of the ware deposited.
 */
@Serializable
data class DepositCreateRequest(
    val userId: String,
    val wareId: String,
    val quantity: Double
)
