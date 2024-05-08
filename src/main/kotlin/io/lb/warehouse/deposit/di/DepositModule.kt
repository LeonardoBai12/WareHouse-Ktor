package io.lb.warehouse.deposit.di

import io.lb.warehouse.deposit.data.repository.DepositRepositoryImpl
import io.lb.warehouse.deposit.data.service.DepositDatabaseService
import io.lb.warehouse.deposit.data.service.DepositDatabaseServiceImpl
import io.lb.warehouse.deposit.domain.repository.DepositRepository
import io.lb.warehouse.deposit.domain.use_cases.CreateDepositUseCase
import io.lb.warehouse.deposit.domain.use_cases.GetDepositByIDUseCase
import io.lb.warehouse.deposit.domain.use_cases.GetDepositsByUserIdUseCase
import io.lb.warehouse.deposit.domain.use_cases.GetDepositsByWareIdUseCase
import io.lb.warehouse.deposit.domain.use_cases.DepositUseCases
import org.koin.dsl.module

val depositModule = module {
    single<DepositDatabaseService> {
        DepositDatabaseServiceImpl(get())
    }
    single<DepositRepository> {
        DepositRepositoryImpl(get())
    }
    single {
        DepositUseCases(
            createDepositUseCase = CreateDepositUseCase(get()),
            getDepositByIDUseCase = GetDepositByIDUseCase(get()),
            getDepositsByUserIdUseCase = GetDepositsByUserIdUseCase(get()),
            getDepositsByWareIdUseCase = GetDepositsByWareIdUseCase(get()),
        )
    }
}
