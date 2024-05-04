package io.lb.warehouse.core.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.jwt.jwt
import java.io.FileInputStream
import java.util.Properties

fun Application.configureAuth() {
    install(Authentication) {
        jwt("jwt") {
            verifier(JwtConfig.verifier)
            validate { credential ->
                if (credential.payload.getClaim("user_id").asInt() > 0) {
                    UserIdPrincipal(credential.payload.getClaim("user_id").asString())
                } else null
            }
        }
    }
}

object JwtConfig {
    private fun getSecret(): String {
        val properties = Properties()
        val fileInputStream = FileInputStream("local.properties")
        properties.load(fileInputStream)
        return properties.getProperty("jwt.secret_key")!!
    }
    private const val issuer = "lb.io"

    val verifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(getSecret()))
        .withIssuer(issuer)
        .build()
}
