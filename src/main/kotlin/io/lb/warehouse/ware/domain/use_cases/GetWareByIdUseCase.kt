package io.lb.warehouse.ware.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.ware.data.model.WareData
import io.lb.warehouse.ware.domain.repository.WareRepository

/**
 * Use case for retrieving a ware by its ID.
 *
 * @property repository The repository for interacting with ware data.
 */
class GetWareByIdUseCase(
    private val repository: WareRepository
) {
    /**
     * Retrieves a ware by its ID.
     *
     * @param id The ID of the ware to retrieve.
     * @return The ware data if found.
     * @throws WareHouseException if no ware is found with the specified ID.
     */
    suspend operator fun invoke(id: String): WareData {
        return repository.getWareById(id) ?: run {
            throw WareHouseException(HttpStatusCode.NotFound, "There is no ware with such ID")
        }
    }
}
