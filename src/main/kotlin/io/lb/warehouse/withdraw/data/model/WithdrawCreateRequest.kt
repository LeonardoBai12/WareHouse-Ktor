package io.lb.warehouse.withdraw.data.model

data class WithdrawCreateRequest(
    val uuid: String,
    val userId: String,
    val wareId: String,
    val quantity: Double
)
