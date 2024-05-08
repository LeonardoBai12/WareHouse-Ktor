package io.lb.warehouse.user.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.user.data.model.UserUpdateRequest
import io.lb.warehouse.user.domain.repository.UserRepository
import io.lb.warehouse.user.util.validateEmail
import io.lb.warehouse.user.util.validatePassword

class UpdateUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String, user: UserUpdateRequest) {
        repository.validateEmail(user.email)
        val storedUser = repository.validatePassword(userId, user.password)

        if (user.userName != null && user.userName.isBlank()) {
            throw WareHouseException(HttpStatusCode.Conflict, "User must have a name.")
        }

        val updatedUser = storedUser.copy(
            userName = user.userName ?: storedUser.userName,
            email = user.email ?: storedUser.email,
            profilePictureUrl = user.profilePictureUrl ?: storedUser.profilePictureUrl,
        )
        repository.updateUser(updatedUser)
    }
}
