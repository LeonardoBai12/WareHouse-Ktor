package io.lb.warehouse.ware.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WareCreateRequest(
    val userId: String,
    val name: String,
    val brand: String,
    val description: String?,
    val weightPerUnit: Double,
    val weightUnit: String?,
    val availableQuantity: Double = 0.0,
    val quantityUnit: String?,
    val wareLocation: String,
)
