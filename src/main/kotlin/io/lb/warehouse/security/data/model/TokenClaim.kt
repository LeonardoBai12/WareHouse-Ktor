package io.lb.warehouse.security.data.model

/**
 * Data class representing a request to generate a tokem.
 *
 * @property name Key name to represent the token claimer.
 * @property value value to represent the token claimer.
 */
data class TokenClaim(
    val name: String,
    val value: String
)
