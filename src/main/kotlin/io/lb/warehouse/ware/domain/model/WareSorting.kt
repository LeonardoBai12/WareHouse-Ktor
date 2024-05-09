package io.lb.warehouse.ware.domain.model

/**
 * Enum class representing the ware kinds of sorting.
 *
 * @property label The label to be used on API parameters.
 *
 * @property BY_NAME Order by name.
 * @property BY_BRAND Order by brand.
 * @property BY_AVAILABLE_QUANTITY Order by quantity.
 * @property BY_TIMESTAMP Order by timestamp.
 */
enum class WareSorting(val label: String) {
    BY_NAME(label = "name"),
    BY_BRAND(label = "brand"),
    BY_AVAILABLE_QUANTITY(label = "quantity"),
    BY_TIMESTAMP(label = "timestamp");

    /**
     * Enum class representing the orders that wares can be ordered.
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
