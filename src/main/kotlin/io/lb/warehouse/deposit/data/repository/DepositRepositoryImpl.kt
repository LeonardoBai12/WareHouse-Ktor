package io.lb.warehouse.deposit.data.repository

import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.data.model.DepositData
import io.lb.warehouse.deposit.data.service.DepositDatabaseService
import io.lb.warehouse.deposit.domain.model.DepositParameters
import io.lb.warehouse.deposit.domain.repository.DepositRepository

/**
 * Repository class for interacting with deposit data.
 */
class DepositRepositoryImpl(
    private val service: DepositDatabaseService
) : DepositRepository {
    override suspend fun insertDeposit(deposit: DepositCreateRequest): String {
        return service.insertDeposit(deposit)
    }

    override suspend fun getDepositById(id: String): DepositData? {
        return service.getDepositById(id)
    }

    override suspend fun getDeposits(parameters: DepositParameters): List<DepositData> {
        return service.getDeposits(parameters.userId, parameters.wareId)
    }
}
