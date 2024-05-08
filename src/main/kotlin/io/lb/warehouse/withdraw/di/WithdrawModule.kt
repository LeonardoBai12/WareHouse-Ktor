package io.lb.warehouse.withdraw.di

import io.lb.warehouse.withdraw.data.repository.WithdrawRepositoryImpl
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseServiceImpl
import io.lb.warehouse.withdraw.domain.repository.WithdrawRepository
import io.lb.warehouse.withdraw.domain.use_cases.CreateWithdrawUseCase
import io.lb.warehouse.withdraw.domain.use_cases.GetWithdrawByIDUseCase
import io.lb.warehouse.withdraw.domain.use_cases.GetWithdrawsByUserIdUseCase
import io.lb.warehouse.withdraw.domain.use_cases.GetWithdrawsByWareIdUseCase
import io.lb.warehouse.withdraw.domain.use_cases.WithdrawUseCases
import org.koin.dsl.module

val withdrawModule = module {
    single<WithdrawDatabaseService> {
        WithdrawDatabaseServiceImpl(get())
    }
    single<WithdrawRepository> {
        WithdrawRepositoryImpl(get())
    }
    single {
        WithdrawUseCases(
            createWithdrawUseCase = CreateWithdrawUseCase(get()),
            getWithdrawByIDUseCase = GetWithdrawByIDUseCase(get()),
            getWithdrawsByUserIdUseCase = GetWithdrawsByUserIdUseCase(get()),
            getWithdrawsByWareIdUseCase = GetWithdrawsByWareIdUseCase(get()),
        )
    }
}
