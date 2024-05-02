package io.lb.warehouse.deposit.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DepositCreateRequest(
    val userId: String,
    val wareId: String,
    val quantity: Double
)
