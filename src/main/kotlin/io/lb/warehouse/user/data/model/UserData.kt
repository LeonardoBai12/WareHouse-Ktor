package io.lb.warehouse.user.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserData(
    val userId: String = UUID.randomUUID().toString(),
    val userName: String,
    val password: String? = null,
    val email: String,
    val profilePictureUrl: String? = null
)
