package io.lb.warehouse.deposit.domain.use_cases

/**
 * Container for deposit-related use cases.
 *
 * @property createDepositUseCase The use case for creating a new deposit.
 * @property getDepositByIDUseCase The use case for retrieving a deposit by its ID.
 * @property getDepositsByUserIdUseCase The use case for retrieving deposits associated with a user by their UUID.
 * @property getDepositsByWareIdUseCase The use case for retrieving deposits associated with a ware by its UUID.
 */
data class DepositUseCases(
    val createDepositUseCase: CreateDepositUseCase,
    val getDepositByIDUseCase: GetDepositByIDUseCase,
    val getDepositsByUserIdUseCase: GetDepositsByUserIdUseCase,
    val getDepositsByWareIdUseCase: GetDepositsByWareIdUseCase
)
