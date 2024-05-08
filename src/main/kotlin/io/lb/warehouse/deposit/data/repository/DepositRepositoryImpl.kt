package io.lb.warehouse.deposit.data.repository

import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.data.model.DepositData
import io.lb.warehouse.deposit.data.service.DepositDatabaseService
import io.lb.warehouse.deposit.domain.repository.DepositRepository

class DepositRepositoryImpl(
    private val service: DepositDatabaseService
) : DepositRepository {
    override suspend fun insertDeposit(deposit: DepositCreateRequest): String {
        return service.insertDeposit(deposit)
    }

    override suspend fun getDepositById(id: String): DepositData? {
        return service.getDepositById(id)
    }

    override suspend fun getDepositsByUserId(userUUID: String): List<DepositData> {
        return service.getDepositsByUserId(userUUID)
    }

    override suspend fun getDepositsByWareId(wareUUID: String): List<DepositData> {
        return service.getDepositsByWareId(wareUUID)
    }
}
