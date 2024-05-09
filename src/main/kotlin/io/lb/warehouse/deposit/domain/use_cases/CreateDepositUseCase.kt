package io.lb.warehouse.deposit.domain.use_cases

import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.domain.repository.DepositRepository

/**
 * Use case for creating a new deposit.
 *
 * @property repository The repository for interacting with deposit data.
 */
class CreateDepositUseCase(
    private val repository: DepositRepository
) {
    /**
     * Creates a new deposit.
     *
     * @param request The deposit creation request.
     * @return The UUID of the newly created deposit.
     */
    suspend operator fun invoke(request: DepositCreateRequest): String {
        return repository.insertDeposit(request)
    }
}
