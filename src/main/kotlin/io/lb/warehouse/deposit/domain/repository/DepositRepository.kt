package io.lb.warehouse.deposit.domain.repository

import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.data.model.DepositData

interface DepositRepository {
    suspend fun insertDeposit(deposit: DepositCreateRequest): String
    suspend fun getDepositById(id: String): DepositData?
    suspend fun getDepositsByUserId(userUUID: String): List<DepositData>
    suspend fun getDepositsByWareId(wareUUID: String): List<DepositData>
}
