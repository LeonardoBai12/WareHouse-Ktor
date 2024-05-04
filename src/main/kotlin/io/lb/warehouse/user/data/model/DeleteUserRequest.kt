package io.lb.warehouse.user.data.model

import kotlinx.serialization.Serializable

/**
 * Data class representing a request to delete a user.
 *
 * @property password The user's password for authentication.
 */
@Serializable
data class DeleteUserRequest(
    val password: String,
)
