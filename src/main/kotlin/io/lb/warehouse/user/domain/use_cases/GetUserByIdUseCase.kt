package io.lb.warehouse.user.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.user.data.model.UserData
import io.lb.warehouse.user.domain.repository.UserRepository

/**
 * Use case for retrieving a user by their ID.
 *
 * @property repository The repository for interacting with user data.
 */
class GetUserByIdUseCase(
    private val repository: UserRepository
) {
    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The user data if found.
     * @throws WareHouseException if no user is found with the specified ID.
     */
    suspend operator fun invoke(userId: String): UserData {
        return repository.getUserById(userId) ?: throw WareHouseException(
            HttpStatusCode.NotFound,
            "There is no user with such ID"
        )
    }
}
