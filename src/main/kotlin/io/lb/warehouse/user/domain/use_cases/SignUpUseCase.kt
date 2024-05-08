package io.lb.warehouse.user.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.extensions.encrypt
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.user.data.model.UserCreateRequest
import io.lb.warehouse.user.data.model.UserData
import io.lb.warehouse.user.domain.repository.UserRepository
import io.lb.warehouse.user.util.validateEmail

/**
 * Use case for user sign up.
 *
 * @property repository The repository for interacting with user data.
 */
class SignUpUseCase(
    private val repository: UserRepository
) {
    /**
     * Registers a new user.
     *
     * @param user The user creation request containing user details.
     * @return The ID of the newly created user.
     * @throws WareHouseException if there are validation errors or if user creation fails.
     */
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
