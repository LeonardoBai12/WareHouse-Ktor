package io.lb.warehouse.withdraw.domain.use_cases

/**
 * Container for withdraw-related use cases.
 *
 * @property createWithdrawUseCase The use case for creating a new withdraw.
 * @property getWithdrawByIDUseCase The use case for retrieving a withdraw by its ID.
 * @property getWithdrawsByUserIdUseCase The use case for retrieving withdraws associated with a user by their UUID.
 * @property getWithdrawsByWareIdUseCase The use case for retrieving withdraws associated with a ware by its UUID.
 */
data class WithdrawUseCases(
    val createWithdrawUseCase: CreateWithdrawUseCase,
    val getWithdrawByIDUseCase: GetWithdrawByIDUseCase,
    val getWithdrawsByUserIdUseCase: GetWithdrawsByUserIdUseCase,
    val getWithdrawsByWareIdUseCase: GetWithdrawsByWareIdUseCase
)
