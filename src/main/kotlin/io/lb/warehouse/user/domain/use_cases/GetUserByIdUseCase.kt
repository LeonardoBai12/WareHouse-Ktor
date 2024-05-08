package io.lb.warehouse.user.domain.use_cases

import io.ktor.http.HttpStatusCode
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.user.data.model.UserData
import io.lb.warehouse.user.domain.repository.UserRepository

class GetUserByIdUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): UserData {
        return repository.getUserById(userId) ?:throw WareHouseException(
            HttpStatusCode.NotFound,
            "There is no user with such ID"
        )
    }
}
