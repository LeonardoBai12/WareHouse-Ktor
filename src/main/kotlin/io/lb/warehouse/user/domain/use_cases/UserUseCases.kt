package io.lb.warehouse.user.domain.use_cases

data class UserUseCases(
    val deleteUserUseCase: DeleteUserUseCase,
    val getUserByIdUseCase: GetUserByIdUseCase,
    val loginUseCase: LoginUseCase,
    val logoutUseCase: LogoutUseCase,
    val signUpUseCase: SignUpUseCase,
    val updatePasswordUseCase: UpdatePasswordUseCase,
    val updateUserUseCase: UpdateUserUseCase,
)
