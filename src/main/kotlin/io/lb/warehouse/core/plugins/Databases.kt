package io.lb.warehouse.core.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import io.lb.warehouse.deposit.data.service.DepositDatabaseService
import io.lb.warehouse.deposit.routes.depositRoutes
import io.lb.warehouse.security.data.model.TokenConfig
import io.lb.warehouse.user.data.service.UserDatabaseService
import io.lb.warehouse.user.routes.userRoutes
import io.lb.warehouse.ware.data.service.WareDatabaseService
import io.lb.warehouse.ware.routes.wareRoutes
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService
import io.lb.warehouse.withdraw.routes.withdrawRoutes
import java.io.FileInputStream
import java.util.Properties

fun Application.configureDatabases(config: TokenConfig) {
    val dbConnection = connectToPostgres(embedded = true).connection

    val userDatabaseService = UserDatabaseService(dbConnection)
    userRoutes(config, userDatabaseService)

    val wareDatabaseService = WareDatabaseService(dbConnection)
    wareRoutes(wareDatabaseService)

    val withdrawDatabaseService = WithdrawDatabaseService(dbConnection)
    withdrawRoutes(withdrawDatabaseService)

    val depositDatabaseService = DepositDatabaseService(dbConnection)
    depositRoutes(depositDatabaseService)
}

fun Application.connectToPostgres(embedded: Boolean): HikariDataSource {
    val hikariConfig = HikariConfig()

    if (embedded) {
        val properties = Properties()
        val fileInputStream = FileInputStream("local.properties")
        properties.load(fileInputStream)

        val databaseUrl = properties.getProperty("database.url")
        val databaseUsername = properties.getProperty("database.username")
        val databasePassword = properties.getProperty("database.password")

        hikariConfig.apply {
            jdbcUrl = databaseUrl
            username = databaseUsername
            password = databasePassword
        }
    } else {
        val databaseUrl = environment.config.property("postgres.url").getString()
        val databaseUsername = environment.config.property("postgres.user").getString()
        val databasePassword = environment.config.property("postgres.password").getString()

        hikariConfig.apply {
            jdbcUrl = databaseUrl
            username = databaseUsername
            password = databasePassword
        }
    }

    return HikariDataSource(hikariConfig)
}
