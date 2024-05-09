package io.lb.warehouse.user.domain.use_cases

import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.user.domain.repository.UserRepository
import io.lb.warehouse.user.util.validatePassword

/**
 * Use case for deleting a user.
 *
 * @property repository The repository for interacting with user data.
 */
class DeleteUserUseCase(
    private val repository: UserRepository
) {
    /**
     * Deletes a user after validating the provided password.
     *
     * @param userId The ID of the user to delete.
     * @param password The password of the user to validate the deletion.
     * @throws WareHouseException if the provided password is invalid.
     */
    suspend operator fun invoke(userId: String, password: String) {
        repository.validatePassword(userId, password)
        repository.deleteUser(userId)
    }
}
