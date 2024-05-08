package io.lb.warehouse.user.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.user.domain.repository.UserRepository
import io.lb.warehouse.user.util.validatePassword

/**
 * Use case for updating user password.
 *
 * @property repository The repository for interacting with user data.
 */
class UpdatePasswordUseCase(
    private val repository: UserRepository
) {
    /**
     * Updates the password for a user.
     *
     * @param userId The ID of the user whose password is being updated.
     * @param password The current password of the user.
     * @param newPassword The new password for the user.
     * @throws WareHouseException if the current password is invalid or if the new password does not meet the requirements.
     */
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
