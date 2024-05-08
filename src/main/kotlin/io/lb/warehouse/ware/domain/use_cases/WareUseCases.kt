package io.lb.warehouse.ware.domain.use_cases

data class WareUseCases(
    val createWareUseCase: CreateWareUseCase,
    val deleteWareUseCase: DeleteWareUseCase,
    val getWareByIdUseCase: GetWareByIdUseCase,
    val getWaresByUserIdUseCase: GetWaresByUserIdUseCase,
    val updateWareUseCase: UpdateWareUseCase
)
