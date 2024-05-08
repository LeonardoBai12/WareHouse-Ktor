package io.lb.warehouse.user.di

import io.lb.warehouse.user.data.repository.UserRepositoryImpl
import io.lb.warehouse.user.data.service.UserDatabaseService
import io.lb.warehouse.user.data.service.UserDatabaseServiceImpl
import io.lb.warehouse.user.domain.repository.UserRepository
import io.lb.warehouse.user.domain.use_cases.DeleteUserUseCase
import io.lb.warehouse.user.domain.use_cases.GetUserByIdUseCase
import io.lb.warehouse.user.domain.use_cases.LoginUseCase
import io.lb.warehouse.user.domain.use_cases.SignUpUseCase
import io.lb.warehouse.user.domain.use_cases.UpdatePasswordUseCase
import io.lb.warehouse.user.domain.use_cases.UpdateUserUseCase
import io.lb.warehouse.user.domain.use_cases.UserUseCases
import org.koin.dsl.module

val userModule = module {
    single<UserDatabaseService> {
        UserDatabaseServiceImpl(get())
    }
    single<UserRepository> {
        UserRepositoryImpl(get())
    }
    single {
        UserUseCases(
            deleteUserUseCase = DeleteUserUseCase(get()),
            getUserByIdUseCase = GetUserByIdUseCase(get()),
            loginUseCase = LoginUseCase(get(), get()),
            signUpUseCase = SignUpUseCase(get()),
            updatePasswordUseCase = UpdatePasswordUseCase(get()),
            updateUserUseCase = UpdateUserUseCase(get()),
        )
    }
}
