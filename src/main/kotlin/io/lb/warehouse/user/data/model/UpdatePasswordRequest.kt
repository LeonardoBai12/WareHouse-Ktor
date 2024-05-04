package io.lb.warehouse.user.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequest(
    val password: String,
    val newPassword: String,
)
