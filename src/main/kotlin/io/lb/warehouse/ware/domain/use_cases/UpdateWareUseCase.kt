package io.lb.warehouse.ware.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.domain.repository.WareRepository

/**
 * Use case for updating a ware.
 *
 * @property repository The repository for interacting with ware data.
 */
class UpdateWareUseCase(
    private val repository: WareRepository
) {
    /**
     * Updates a ware by its ID.
     *
     * @param id The ID of the ware to update.
     * @param ware The updated ware data.
     * @throws WareHouseException if no ware is found with the specified ID.
     */
    suspend operator fun invoke(id: String, ware: WareCreateRequest) {
        repository.getWareById(id) ?: run {
            throw WareHouseException(HttpStatusCode.NotFound, "There is no ware with such ID")
        }

        repository.updateWare(id, ware)
    }
}
