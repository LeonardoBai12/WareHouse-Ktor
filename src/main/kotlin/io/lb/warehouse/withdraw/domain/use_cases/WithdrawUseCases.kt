package io.lb.warehouse.withdraw.domain.use_cases

/**
 * Container for withdraw-related use cases.
 *
 * @property createWithdrawUseCase The use case for creating a new withdrawals.
 * @property getWithdrawByIDUseCase The use case for retrieving a withdrawals by its ID.
 * @property getWithdrawsUseCase The use case for retrieving withdraws.
 */
data class WithdrawUseCases(
    val createWithdrawUseCase: CreateWithdrawUseCase,
    val getWithdrawByIDUseCase: GetWithdrawByIDUseCase,
    val getWithdrawsUseCase: GetWithdrawsUseCase,
)
