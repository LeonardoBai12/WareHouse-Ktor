package io.lb.warehouse.core.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.lb.warehouse.core.session.WarehouseSession

private const val SESSION_NAME = "WareHouseSessions"

fun Application.configureSession() {
    install(Sessions) {
        cookie<WarehouseSession>(SESSION_NAME)
    }
}
