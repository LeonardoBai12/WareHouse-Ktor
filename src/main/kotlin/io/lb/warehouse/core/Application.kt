package io.lb.warehouse.core

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.lb.warehouse.core.di.configureInjection
import io.lb.warehouse.core.plugins.configureAuth
import io.lb.warehouse.core.plugins.configureDatabases
import io.lb.warehouse.core.plugins.configureMonitoring
import io.lb.warehouse.core.plugins.configureSerialization
import io.lb.warehouse.core.plugins.configureSession

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
    configureInjection()
    configureSerialization()
    configureMonitoring()
    configureSession()
    configureAuth()
    configureDatabases()
}
