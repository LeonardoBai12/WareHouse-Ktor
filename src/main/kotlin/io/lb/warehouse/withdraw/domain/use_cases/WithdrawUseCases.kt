package io.lb.warehouse.withdraw.domain.use_cases

data class WithdrawUseCases(
    val createWithdrawUseCase: CreateWithdrawUseCase,
    val getWithdrawByIDUseCase: GetWithdrawByIDUseCase,
    val getWithdrawsByUserIdUseCase: GetWithdrawsByUserIdUseCase,
    val getWithdrawsByWareIdUseCase: GetWithdrawsByWareIdUseCase
)
