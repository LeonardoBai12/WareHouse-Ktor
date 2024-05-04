package io.lb.warehouse.user.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserCreateRequest(
    val userName: String,
    val password: String,
    val email: String,
    val profilePictureUrl: String? = null
)
