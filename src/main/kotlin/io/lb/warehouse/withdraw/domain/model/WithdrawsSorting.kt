package io.lb.warehouse.withdraw.domain.model

/**
 * Enum class representing the withdrawal kinds of sorting.
 *
 * @property label The label to be used on API parameters.
 *
 * @property BY_USER Order by user UUID.
 * @property BY_WARE Order by ware UUID.
 * @property BY_QUANTITY Order by quantity.
 * @property BY_TIMESTAMP Order by timestamp.
 */
enum class WithdrawsSorting(val label: String) {
    BY_USER(label = "userId"),
    BY_WARE(label = "wareId"),
    BY_QUANTITY(label = "quantity"),
    BY_TIMESTAMP(label = "timestamp");

    /**
     * Enum class representing the orders that withdrawals can be ordered.
     *
     * @property label The label to be used on API parameters.
     *
     * @property ASCENDING Sort by ascending.
     * @property DESCENDING Sort by descending.
     */
    enum class SortOrder(val label: String) {
        ASCENDING(label = "asc"),
        DESCENDING(label = "desc"),
    }
}
