package io.lb.warehouse.withdraw.util

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.withdraw.data.model.WithdrawData
import io.lb.warehouse.withdraw.domain.model.WithdrawsSorting

/**
 * Sorts a list of withdraw data based on the specified sorting criteria and order.
 *
 * @param sortBy The field to sort the withdraws by.
 * @param order The order in which to sort the withdraws (ascending or descending).
 * @return A sorted list of withdraw data.
 * @throws WareHouseException if the sorting criteria or order is invalid.
 */
fun List<WithdrawData>.getOrderedWithdraws(
    sortBy: String,
    order: String,
): List<WithdrawData> {
    return when (order) {
        WithdrawsSorting.SortOrder.ASCENDING.label -> {
            sortedBy {
                it.getSortingTypeByLabel(sortBy)
            }
        }

        WithdrawsSorting.SortOrder.DESCENDING.label -> {
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
 * Retrieves the sorting type of a withdraw data object based on the specified sorting criteria.
 *
 * @param sortBy The sorting criteria.
 * @return The sorting type for the withdraw data object.
 * @throws WareHouseException if the sorting criteria is invalid.
 */
private fun WithdrawData.getSortingTypeByLabel(sortBy: String) = when (sortBy) {
    WithdrawsSorting.BY_USER.label -> {
        userId
    }

    WithdrawsSorting.BY_WARE.label -> {
        wareId
    }

    WithdrawsSorting.BY_QUANTITY.label -> {
        quantity.toString()
    }

    WithdrawsSorting.BY_TIMESTAMP.label -> {
        timestamp
    }

    else -> {
        throw WareHouseException(
            HttpStatusCode.BadRequest,
            "Sorting should be: [userId, wareId, quantity, timestamp]"
        )
    }
}
