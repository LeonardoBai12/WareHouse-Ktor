package io.lb.warehouse.ware.util

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.ware.data.model.WareData
import io.lb.warehouse.ware.domain.model.WareSorting

/**
 * Sorts a list of ware data based on the specified sorting criteria and order.
 *
 * @param sortBy The field to sort the wares by.
 * @param order The order in which to sort the wares (ascending or descending).
 * @return A sorted list of ware data.
 * @throws WareHouseException if the sorting criteria or order is invalid.
 */
fun List<WareData>.getOrderedWares(
    sortBy: String,
    order: String,
): List<WareData> {
    return when (order) {
        WareSorting.SortOrder.ASCENDING.label -> {
            sortedBy {
                it.getSortingTypeByLabel(sortBy)
            }
        }

        WareSorting.SortOrder.DESCENDING.label -> {
            sortedByDescending {
                it.getSortingTypeByLabel(sortBy)
            }
        }

        else -> {
            throw WareHouseException(
                HttpStatusCode.BadRequest,
                "Order should be: [asc, desc]"
            )
        }
    }
}

/**
 * Retrieves the sorting type of ware data object based on the specified sorting criteria.
 *
 * @param sortBy The sorting criteria.
 * @return The sorting type for the ware data object.
 * @throws WareHouseException if the sorting criteria is invalid.
 */
private fun WareData.getSortingTypeByLabel(sortBy: String) = when (sortBy) {
    WareSorting.BY_NAME.label -> {
        name
    }

    WareSorting.BY_BRAND.label -> {
        brand
    }

    WareSorting.BY_AVAILABLE_QUANTITY.label -> {
        availableQuantity.toString()
    }

    WareSorting.BY_TIMESTAMP.label -> {
        timestamp
    }

    else -> {
        throw WareHouseException(
            HttpStatusCode.BadRequest,
            "Sorting should be: [name, brand, quantity, timestamp]"
        )
    }
}
