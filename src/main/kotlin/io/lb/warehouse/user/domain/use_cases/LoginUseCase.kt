package io.lb.warehouse.user.domain.use_cases

import io.lb.warehouse.security.data.model.TokenClaim
import io.lb.warehouse.security.data.model.TokenConfig
import io.lb.warehouse.security.generateToken
import io.lb.warehouse.user.domain.repository.UserRepository
import io.lb.warehouse.user.util.validatePassword

class LoginUseCase(
    private val repository: UserRepository,
    private val tokenConfig: TokenConfig
) {
    suspend operator fun invoke(userId: String, password: String): String {
        repository.validatePassword(userId, password)

        return generateToken(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = userId
            )
        )
    }
}
