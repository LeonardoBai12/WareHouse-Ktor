package io.lb.warehouse.withdraw.data.model

import kotlinx.serialization.Serializable


@Serializable
data class WithdrawData(
    val uuid: String,
    val userId: String,
    val wareId: String,
    val quantity: Double,
)
