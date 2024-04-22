package io.lb.warehouse.core.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.util.generateNonce
import io.lb.warehouse.core.session.WarehouseSession

private const val SESSION_NAME = "SESSIONS"
private const val USER_ID = "user_id"

fun Application.configureSession() {
    install(Sessions) {
        cookie<WarehouseSession>(SESSION_NAME)
    }

    intercept(ApplicationCallPipeline.Plugins) {
        call.sessions.get<WarehouseSession>() ?: {
            val clientId = call.parameters[USER_ID] ?: ""
            call.sessions.set(
                WarehouseSession(
                    clientId = clientId,
                    sessionId = generateNonce()
                )
            )
        }
    }
}
