package io.lb.warehouse.ware.domain.use_cases

/**
 * Container for ware-related use cases.
 *
 * @property createWareUseCase The use case for creating a new ware.
 * @property deleteWareUseCase The use case for deleting a ware.
 * @property getWareByIdUseCase The use case for retrieving a ware by its ID.
 * @property getWaresByUserIdUseCase The use case for retrieving wares associated with a user by their ID.
 * @property updateWareUseCase The use case for updating a ware.
 */
data class WareUseCases(
    val createWareUseCase: CreateWareUseCase,
    val deleteWareUseCase: DeleteWareUseCase,
    val getWareByIdUseCase: GetWareByIdUseCase,
    val getWaresByUserIdUseCase: GetWaresByUserIdUseCase,
    val updateWareUseCase: UpdateWareUseCase
)
