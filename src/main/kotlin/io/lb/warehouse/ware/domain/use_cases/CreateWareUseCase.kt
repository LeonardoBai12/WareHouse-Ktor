package io.lb.warehouse.ware.domain.use_cases

import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.domain.repository.WareRepository

/**
 * Use case for creating a new ware.
 *
 * @property repository The repository for interacting with ware data.
 */
class CreateWareUseCase(
    private val repository: WareRepository
) {
    /**
     * Creates a new ware.
     *
     * @param ware The ware data to insert.
     * @return The UUID of the newly created ware.
     */
    suspend operator fun invoke(ware: WareCreateRequest): String {
        return repository.insertWare(ware)
    }
}
