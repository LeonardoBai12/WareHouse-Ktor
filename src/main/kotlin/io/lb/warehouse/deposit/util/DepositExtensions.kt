package io.lb.warehouse.deposit.util

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.deposit.data.model.DepositData
import io.lb.warehouse.deposit.domain.model.DepositsSorting

/**
 * Sorts a list of deposit data based on the specified sorting criteria and order.
 *
 * @param sortBy The field to sort the deposits by.
 * @param order The order in which to sort the deposits (ascending or descending).
 * @return A sorted list of deposit data.
 * @throws WareHouseException if the sorting criteria or order is invalid.
 */
fun List<DepositData>.getOrderedDeposits(
    sortBy: String,
    order: String,
): List<DepositData> {
    return when (order) {
        DepositsSorting.SortOrder.ASCENDING.label -> {
            sortedBy {
                it.getSortingTypeByLabel(sortBy)
            }
        }

        DepositsSorting.SortOrder.DESCENDING.label -> {
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
 * Retrieves the sorting type of a deposit data object based on the specified sorting criteria.
 *
 * @param sortBy The sorting criteria.
 * @return The sorting type for the deposit data object.
 * @throws WareHouseException if the sorting criteria is invalid.
 */
private fun DepositData.getSortingTypeByLabel(sortBy: String) = when (sortBy) {
    DepositsSorting.BY_USER.label -> {
        userId
    }

    DepositsSorting.BY_WARE.label -> {
        wareId
    }

    DepositsSorting.BY_QUANTITY.label -> {
        quantity.toString()
    }

    DepositsSorting.BY_TIMESTAMP.label -> {
        timestamp
    }

    else -> {
        throw WareHouseException(
            HttpStatusCode.BadRequest,
            "Sorting should be: [userId, wareId, quantity, timestamp]"
        )
    }
}
