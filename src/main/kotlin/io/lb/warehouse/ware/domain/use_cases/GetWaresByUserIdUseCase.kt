package io.lb.warehouse.ware.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.ware.data.model.WareData
import io.lb.warehouse.ware.domain.repository.WareRepository
import io.lb.warehouse.ware.util.getOrderedWares

/**
 * Use case for retrieving wares associated with a user by their ID.
 *
 * @property repository The repository for interacting with ware data.
 */
class GetWaresByUserIdUseCase(
    private val repository: WareRepository
) {
    /**
     * Retrieves wares associated with a user by their ID, sorted and ordered as specified.
     *
     * @param userId The ID of the user.
     * @param sortBy The field to sort the wares by.
     * @param order The order in which to sort the wares (ascending or descending).
     * @return A list of wares associated with the user, sorted and ordered as specified.
     * @throws WareHouseException if no wares are found for the specified user.
     */
    suspend operator fun invoke(userId: String, sortBy: String, order: String): List<WareData> {
        val result = repository.getWaresByUserId(userId)

        if (result.isEmpty()) {
            throw WareHouseException(HttpStatusCode.NotFound, "There are no wares for such user")
        }

        return result.getOrderedWares(sortBy, order)
    }
}
