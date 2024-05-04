package io.lb.warehouse.deposit.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DepositData(
    val uuid: String,
    val userId: String,
    val wareId: String,
    val quantity: Double,
)
