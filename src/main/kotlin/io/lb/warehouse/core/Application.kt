package io.lb.warehouse.core

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.lb.warehouse.core.plugins.configureAuth
import io.lb.warehouse.core.plugins.configureDatabases
import io.lb.warehouse.core.plugins.configureMonitoring
import io.lb.warehouse.core.plugins.configureSerialization
import io.lb.warehouse.core.plugins.configureSession
import io.lb.warehouse.security.data.model.TokenConfig

/**
 * Main function of the server.
 */
fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

/**
 * Application module configuration.
 */
fun Application.module() {
    val config = TokenConfig.wareHouseTokenConfig(
        config = environment.config,
        embedded = true
    )

    configureSerialization()
    configureMonitoring()
    configureSession()
    configureAuth(config)
    configureDatabases(config)
}
