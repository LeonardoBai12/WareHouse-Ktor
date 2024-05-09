package io.lb.warehouse.ware.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.ware.domain.repository.WareRepository

/**
 * Use case for deleting a ware.
 *
 * @property repository The repository for interacting with ware data.
 */
class DeleteWareUseCase(
    private val repository: WareRepository
) {
    /**
     * Deletes a ware by its ID.
     *
     * @param id The ID of the ware to delete.
     * @throws WareHouseException if no ware is found with the specified ID.
     */
    suspend operator fun invoke(id: String) {
        repository.getWareById(id) ?: run {
            throw WareHouseException(HttpStatusCode.NotFound, "There is no ware with such ID")
        }

        repository.deleteWare(id)
    }
}
