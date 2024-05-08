package io.lb.warehouse.user.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.user.domain.repository.UserRepository
import io.lb.warehouse.user.util.validatePassword

class UpdatePasswordUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        userId: String,
        password: String,
        newPassword: String
    ) {
        repository.validatePassword(userId, password)

        if (newPassword.length < 8) {
            throw WareHouseException(
                HttpStatusCode.Conflict,
                "Password must have more than 8 characters."
            )
        }

        repository.updatePassword(userId, newPassword)
    }
}
