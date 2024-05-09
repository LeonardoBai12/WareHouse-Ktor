package io.lb.warehouse.withdraw.domain.use_cases

import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.domain.repository.WithdrawRepository

/**
 * Use case for creating a new withdraw.
 *
 * @property repository The repository for interacting with withdraw data.
 */
class CreateWithdrawUseCase(
    private val repository: WithdrawRepository
) {
    /**
     * Creates a new withdraw.
     *
     * @param request The withdraw creation request.
     * @return The ID of the newly created withdraw.
     */
    suspend operator fun invoke(request: WithdrawCreateRequest): String {
        return repository.insertWithdraw(request)
    }
}
