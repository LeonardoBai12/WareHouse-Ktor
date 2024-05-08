package io.lb.warehouse.user.util

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.extensions.passwordCheck
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.user.data.model.UserData
import io.lb.warehouse.user.domain.repository.UserRepository

suspend fun UserRepository.validateEmail(email: String?) {
    if (email != null && isEmailAlreadyInUse(email)) {
        throw WareHouseException(
            HttpStatusCode.Conflict,
            "Email already in use by another user."
        )
    }
}

suspend fun UserRepository.validatePassword(
    userId: String,
    password: String,
) : UserData {
    val storedUser = getUserById(userId) ?: run {
        throw WareHouseException(HttpStatusCode.NotFound, "There is no user with such ID")
    }

    password.ifEmpty {
        throw WareHouseException(HttpStatusCode.Unauthorized, "Invalid password")
    }

    if (password.passwordCheck(storedUser.password!!)) {
        throw WareHouseException(HttpStatusCode.Unauthorized, "Invalid password")
    }

    return storedUser
}
