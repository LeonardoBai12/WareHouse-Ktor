package io.lb.warehouse.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.lb.warehouse.security.data.model.TokenClaim
import io.lb.warehouse.security.data.model.TokenConfig
import java.sql.Date

fun generateToken(config: TokenConfig, vararg claims: TokenClaim): String {
    var token = JWT.create()
        .withAudience(config.audience)
        .withIssuer(config.issuer)
        .withExpiresAt(Date(System.currentTimeMillis() + config.expiresIn))
    claims.forEach { claim ->
        token = token.withClaim(claim.name, claim.value)
    }
    return token.sign(Algorithm.HMAC256(config.secret))
}
