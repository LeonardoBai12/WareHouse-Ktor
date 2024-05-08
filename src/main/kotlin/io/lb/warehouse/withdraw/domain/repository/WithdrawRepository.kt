package io.lb.warehouse.withdraw.domain.repository

import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.data.model.WithdrawData

interface WithdrawRepository {
    suspend fun insertWithdraw(withdraw: WithdrawCreateRequest): String
    suspend fun getWithdrawById(id: String): WithdrawData?
    suspend fun getWithdrawsByUserId(userUUID: String): List<WithdrawData>
    suspend fun getWithdrawsByWareId(wareUUID: String): List<WithdrawData>
}
