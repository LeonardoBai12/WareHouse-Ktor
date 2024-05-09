package io.lb.warehouse.ware.data.repository

import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.data.model.WareData
import io.lb.warehouse.ware.data.service.WareDatabaseService
import io.lb.warehouse.ware.domain.repository.WareRepository

/**
 * Repository class for interacting with ware data.
 */
class WareRepositoryImpl(
    private val service: WareDatabaseService
) : WareRepository {
    override suspend fun insertWare(ware: WareCreateRequest): String {
        return service.insertWare(ware)
    }

    override suspend fun getWareById(id: String): WareData? {
        return service.getWareById(id)
    }

    override suspend fun getWaresByUserId(userUUID: String): List<WareData> {
        return service.getWaresByUserId(userUUID)
    }

    override suspend fun updateWare(uuid: String, ware: WareCreateRequest): Int {
        return service.updateWare(uuid, ware)
    }

    override suspend fun deleteWare(id: String): Int {
        return service.deleteWare(id)
    }
}
