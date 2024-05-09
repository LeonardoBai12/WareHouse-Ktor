package io.lb.warehouse.user.data.model

import kotlinx.serialization.Serializable

/**
 * Data class representing a request to login.
 *
 * @property userId The user's UUID.
 * @property token The user's login token.
 */
@Serializable
data class LoginResponse(
    val userId: String,
    val token: String,
)
