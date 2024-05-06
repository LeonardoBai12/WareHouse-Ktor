package io.lb.warehouse.util

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.basicAuth
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authentication
import io.ktor.server.auth.basic
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.util.generateNonce
import io.lb.warehouse.core.session.WarehouseSession

fun ApplicationTestBuilder.setupApplication(block: Application.() -> Unit) {
    install(ContentNegotiation) {
        json()
        gson {
        }
    }

    application {
        setupApplication()
        block()
    }
}


fun Application.setupApplication() {
    configureAuth()
}

fun Application.configureAuth() {
    authentication {
        basic {
            validate {
                UserIdPrincipal("UserName")
            }
        }
    }
}

fun Application.configureSession(
    bypass: Boolean = true,
    userId: String = ""
) {
    install(Sessions) {
        cookie<WarehouseSession>("WareHouse-Test")
    }
    intercept(ApplicationCallPipeline.Call) {
        call.sessions.get<WarehouseSession>() ?: run {
            if (!bypass) return@intercept

            val clientId = userId.ifBlank { call.parameters["userId"] ?: "" }

            call.sessions.set(
                WarehouseSession(
                    clientId = clientId,
                    sessionId = generateNonce()
                )
            )
        }
    }
}


fun HttpRequestBuilder.setupRequest() {
    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    basicAuth("basic", "auth")
}
