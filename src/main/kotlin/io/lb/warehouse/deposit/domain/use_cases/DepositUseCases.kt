package io.lb.warehouse.deposit.domain.use_cases

/**
 * Container for deposit-related use cases.
 *
 * @property createDepositUseCase The use case for creating a new deposit.
 * @property getDepositByIDUseCase The use case for retrieving a deposit by its ID.
 * @property getDepositsUseCase The use case for retrieving deposits.
 */
data class DepositUseCases(
    val createDepositUseCase: CreateDepositUseCase,
    val getDepositByIDUseCase: GetDepositByIDUseCase,
    val getDepositsUseCase: GetDepositsUseCase,
)
