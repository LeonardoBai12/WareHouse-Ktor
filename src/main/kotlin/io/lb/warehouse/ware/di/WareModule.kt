package io.lb.warehouse.ware.di

import io.lb.warehouse.ware.data.repository.WareRepositoryImpl
import io.lb.warehouse.ware.data.service.WareDatabaseService
import io.lb.warehouse.ware.data.service.WareDatabaseServiceImpl
import io.lb.warehouse.ware.domain.repository.WareRepository
import io.lb.warehouse.ware.domain.use_cases.CreateWareUseCase
import io.lb.warehouse.ware.domain.use_cases.DeleteWareUseCase
import io.lb.warehouse.ware.domain.use_cases.GetWareByIdUseCase
import io.lb.warehouse.ware.domain.use_cases.GetWaresUseCase
import io.lb.warehouse.ware.domain.use_cases.UpdateWareUseCase
import io.lb.warehouse.ware.domain.use_cases.WareUseCases
import org.koin.dsl.module

val wareModule = module {
    single<WareDatabaseService> {
        WareDatabaseServiceImpl(get())
    }
    single<WareRepository> {
        WareRepositoryImpl(get())
    }
    single {
        WareUseCases(
            createWareUseCase = CreateWareUseCase(get()),
            deleteWareUseCase = DeleteWareUseCase(get()),
            getWareByIdUseCase = GetWareByIdUseCase(get()),
            getWaresUseCase = GetWaresUseCase(get()),
            updateWareUseCase = UpdateWareUseCase(get()),
        )
    }
}
