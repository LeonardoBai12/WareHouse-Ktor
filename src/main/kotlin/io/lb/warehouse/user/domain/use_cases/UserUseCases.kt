package io.lb.warehouse.user.domain.use_cases

/**
 * Container for user-related use cases.
 *
 * @property deleteUserUseCase The use case for deleting a user.
 * @property getUserByIdUseCase The use case for retrieving a user by ID.
 * @property loginUseCase The use case for user login.
 * @property signUpUseCase The use case for user sign up.
 * @property updatePasswordUseCase The use case for updating user password.
 * @property updateUserUseCase The use case for updating user information.
 */
data class UserUseCases(
    val deleteUserUseCase: DeleteUserUseCase,
    val getUserByIdUseCase: GetUserByIdUseCase,
    val loginUseCase: LoginUseCase,
    val signUpUseCase: SignUpUseCase,
    val updatePasswordUseCase: UpdatePasswordUseCase,
    val updateUserUseCase: UpdateUserUseCase,
)
