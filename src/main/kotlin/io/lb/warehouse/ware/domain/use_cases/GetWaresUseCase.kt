package io.lb.warehouse.ware.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.ware.data.model.WareData
import io.lb.warehouse.ware.domain.model.WareParameters
import io.lb.warehouse.ware.domain.repository.WareRepository
import io.lb.warehouse.ware.util.getOrderedWares

/**
 * Use case for retrieving wares associated with a user by their ID.
 *
 * @property repository The repository for interacting with ware data.
 */
class GetWaresUseCase(
    private val repository: WareRepository
) {
    /**
     * Retrieves wares associated with a user by their ID, sorted and ordered as specified.
     *
     * @param parameters Parameters used for querying ware data.
     * @return A list of wares associated with the user, sorted and ordered as specified.
     * @throws WareHouseException if no wares are found for the specified filters.
     */
    suspend operator fun invoke(
        parameters: WareParameters
    ): List<WareData> {
        val result = repository.getWares(parameters)

        if (result.isEmpty()) {
            throw WareHouseException(
                HttpStatusCode.NotFound,
                "There are no wares for such filters"
            )
        }

        return result.getOrderedWares(parameters.sortBy, parameters.order)
    }
}
