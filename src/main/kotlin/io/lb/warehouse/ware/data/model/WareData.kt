package io.lb.warehouse.ware.data.model

import kotlinx.serialization.Serializable

/**
 * Data class representing ware data.
 *
 * @property uuid The unique identifier for the ware.
 * @property userId The ID of the user who owns the ware.
 * @property name The name of the ware.
 * @property brand The brand of the ware (optional).
 * @property description The description of the ware (optional).
 * @property weightPerUnit The weight per unit of the ware.
 * @property weightUnit The unit of weight for the ware (optional).
 * @property availableQuantity The available quantity of the ware (default: 0.0).
 * @property quantityUnit The unit of quantity for the ware (optional).
 * @property wareLocation The location of the ware.
 * @property timestamp The timestamp on which the ware was created.
 */
@Serializable
data class WareData(
    val uuid: String,
    val userId: String,
    val name: String,
    val brand: String?,
    val description: String?,
    val weightPerUnit: Double,
    val weightUnit: String?,
    val availableQuantity: Double = 0.0,
    val quantityUnit: String?,
    val wareLocation: String,
    val timestamp: String
)
