package io.lb.warehouse.util

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.basicAuth
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.Principal
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.UserPasswordCredential
import io.ktor.server.auth.authentication
import io.ktor.server.auth.basic
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.ApplicationTestBuilder
import io.lb.warehouse.core.plugins.configureSession
import io.mockk.called
import io.mockk.every

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
    configureSession()
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

fun HttpRequestBuilder.setupRequest() {
    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    basicAuth("basic", "auth")
}
