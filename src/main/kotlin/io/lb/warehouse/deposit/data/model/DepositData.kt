package io.lb.warehouse.deposit.data.model

import kotlinx.serialization.Serializable

/**
 * Serializable data class representing deposit response data.
 *
 * @property uuid Unique identifier for the deposit data.
 * @property userId User identifier associated with the deposit.
 * @property wareId Identifier for the ware associated with the deposit.
 * @property quantity Quantity of the ware deposited.
 */
@Serializable
data class DepositData(
    val uuid: String,
    val userId: String,
    val wareId: String,
    val quantity: Double,
)
