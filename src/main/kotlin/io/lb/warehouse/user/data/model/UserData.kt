package io.lb.warehouse.user.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Data class representing user data.
 *
 * @property userId The unique identifier for the user.
 * @property userName The username.
 * @property password The user's password (returns null on selects).
 * @property email The user's email address.
 * @property profilePictureUrl The URL of the user's profile picture (optional).
 */
@Serializable
data class UserData(
    val userId: String = UUID.randomUUID().toString(),
    val userName: String,
    val password: String? = null,
    val email: String,
    val profilePictureUrl: String? = null
)
