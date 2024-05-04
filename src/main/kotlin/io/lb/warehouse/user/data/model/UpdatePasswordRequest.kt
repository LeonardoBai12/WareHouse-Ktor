package io.lb.warehouse.user.data.model

import kotlinx.serialization.Serializable

/**
 * Data class representing a request to update a user's password.
 *
 * @property password The current password.
 * @property newPassword The new password.
 */
@Serializable
data class UpdatePasswordRequest(
    val password: String,
    val newPassword: String,
)
