package io.lb.warehouse.ware.data.model

data class WareCreateRequest(
    val userId: String,
    val name: String,
    val description: String?,
    val weightPerUnit: Double,
    val weightUnit: String?,
    val availableQuantity: Double = 0.0,
    val quantityUnit: String?,
    val wareLocation: String,
)
