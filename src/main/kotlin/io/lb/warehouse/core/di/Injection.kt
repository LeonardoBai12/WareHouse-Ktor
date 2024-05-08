package io.lb.warehouse.core.di

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.lb.warehouse.core.plugins.connectToPostgres
import io.lb.warehouse.deposit.di.depositModule
import io.lb.warehouse.security.data.model.TokenConfig
import io.lb.warehouse.user.di.userModule
import io.lb.warehouse.ware.di.wareModule
import io.lb.warehouse.withdraw.di.withdrawModule
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureInjection() {
    install(Koin) {
        slf4jLogger()

        module {
            single {
                TokenConfig.wareHouseTokenConfig(
                    config = environment.config,
                    embedded = true
                )
            }
            single {
                connectToPostgres(embedded = true).connection
            }
        }

        modules(
            userModule,
            wareModule,
            withdrawModule,
            depositModule
        )
    }
}
