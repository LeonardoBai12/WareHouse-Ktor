package io.lb.warehouse.deposit.domain.use_cases

data class DepositUseCases(
    val createDepositUseCase: CreateDepositUseCase,
    val getDepositByIDUseCase: GetDepositByIDUseCase,
    val getDepositsByUserIdUseCase: GetDepositsByUserIdUseCase,
    val getDepositsByWareIdUseCase: GetDepositsByWareIdUseCase
)
