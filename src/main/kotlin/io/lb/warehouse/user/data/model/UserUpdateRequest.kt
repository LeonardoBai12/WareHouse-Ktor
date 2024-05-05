package io.lb.warehouse.user.data.model

import kotlinx.serialization.Serializable

/**
 * Data class representing a request to create a new user.
 *
 * @property userName The username.
 * @property password The user's password.
 * @property email The user's email address (optional).
 * @property profilePictureUrl The URL of the user's profile picture (optional).
 */
@Serializable
data class UserUpdateRequest(
    val userName: String? = null,
    val password: String,
    val email: String? = null,
    val profilePictureUrl: String? = null
)
