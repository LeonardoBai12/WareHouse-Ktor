package io.lb.warehouse.withdraw.data.repository

import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.data.model.WithdrawData
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService
import io.lb.warehouse.withdraw.domain.repository.WithdrawRepository

/**
 * Repository class for interacting with withdrawal data.
 */
class WithdrawRepositoryImpl(
    private val service: WithdrawDatabaseService
) : WithdrawRepository {
    override suspend fun insertWithdraw(withdraw: WithdrawCreateRequest): String {
        return service.insertWithdraw(withdraw)
    }

    override suspend fun getWithdrawById(id: String): WithdrawData? {
        return service.getWithdrawById(id)
    }

    override suspend fun getWithdrawsByUserId(userUUID: String): List<WithdrawData> {
        return service.getWithdrawsByUserId(userUUID)
    }

    override suspend fun getWithdrawsByWareId(wareUUID: String): List<WithdrawData> {
        return service.getWithdrawsByWareId(wareUUID)
    }
}
