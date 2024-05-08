package io.lb.warehouse.user.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.extensions.encrypt
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.user.data.model.UserCreateRequest
import io.lb.warehouse.user.data.model.UserData
import io.lb.warehouse.user.domain.repository.UserRepository
import io.lb.warehouse.user.util.validateEmail

class SignUpUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(user: UserCreateRequest): String {
        repository.validateEmail(user.email)

        if (user.userName.isBlank()) {
            throw WareHouseException(HttpStatusCode.Conflict, "User must have a name.")
        }

        if (user.password.length < 8) {
            throw WareHouseException(
                HttpStatusCode.Conflict,
                "Password must have more than 8 characters."
            )
        }

        val hashedPassword = user.password.encrypt()
        val userData = UserData(
            userName = user.userName,
            password = hashedPassword,
            email = user.email,
            profilePictureUrl = user.profilePictureUrl,
        )
        repository.createUser(userData)
        return userData.userId
    }
}
