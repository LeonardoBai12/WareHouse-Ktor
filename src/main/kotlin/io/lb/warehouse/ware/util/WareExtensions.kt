package io.lb.warehouse.ware.util

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.ware.data.model.WareData
import io.lb.warehouse.ware.data.model.WareSorting

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
