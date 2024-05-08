package io.lb.warehouse.ware.domain.repository

import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.data.model.WareData

interface WareRepository {
    suspend fun insertWare(ware: WareCreateRequest): String
    suspend fun getWareById(id: String): WareData?
    suspend fun getWaresByUserId(userUUID: String): List<WareData>
    suspend fun updateWare(uuid: String, ware: WareCreateRequest): Int
    suspend fun deleteWare(id: String): Int
}
