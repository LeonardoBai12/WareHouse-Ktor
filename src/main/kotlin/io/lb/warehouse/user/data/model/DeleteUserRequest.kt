package io.lb.warehouse.user.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeleteUserRequest(
    val password: String,
)
