package io.lb.warehouse.user.domain.use_cases

import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.security.data.model.TokenClaim
import io.lb.warehouse.security.data.model.TokenConfig
import io.lb.warehouse.security.generateToken
import io.lb.warehouse.user.data.model.LoginResponse
import io.lb.warehouse.user.domain.repository.UserRepository
import io.lb.warehouse.user.util.validatePasswordByEmail

/**
 * Use case for user login.
 *
 * @property repository The repository for interacting with user data.
 * @property tokenConfig The configuration for generating authentication tokens.
 */
class LoginUseCase(
    private val repository: UserRepository,
    private val tokenConfig: TokenConfig
) {
    /**
     * Authenticates a user and generates an authentication token.
     *
     * @param email The email of the user attempting to log in.
     * @param password The password provided by the user.
     * @return An authentication token.
     * @throws WareHouseException if the provided email is invalid.
     * @throws WareHouseException if the provided password is invalid.
     */
    suspend operator fun invoke(email: String, password: String): LoginResponse {
        val user = repository.validatePasswordByEmail(email, password)

        return LoginResponse(
            userId = user.userId,
            token = generateToken(
                config = tokenConfig,
                TokenClaim(
                    name = "userId",
                    value = user.userId
                )
            )
        )
    }
}
