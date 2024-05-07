package io.lb.warehouse.security.data.model

import io.ktor.server.config.ApplicationConfig
import java.io.FileInputStream
import java.util.Properties

/**
 * Data class representing token configurations.
 *
 * @param issuer Domain of the tokens to be generated.
 * @param audience Audience of the tokens to be generated.
 * @param expiresIn Timestamp in millis of the experitation date of the token.
 * @param secret Secret key of the token configuration.
 */
data class TokenConfig(
    val issuer: String,
    val audience: String,
    val expiresIn: Long,
    val secret: String
) {
    companion object {
        /**
         * Default TokenConfig for the WareHouse project.
         *
         * @param config Represents an application config node.
         * @param embedded Represents whether the server is embbeded.
         *
         * @return A TokenConfig instance with the default values for the WareHouse project.
         */
        fun wareHouseTokenConfig(
            config: ApplicationConfig,
            embedded: Boolean
        ): TokenConfig {
            val secret = if (embedded) {
                val properties = Properties()
                val fileInputStream = FileInputStream("local.properties")
                properties.load(fileInputStream)

                properties.getProperty("jwt.secret_key")
            } else {
                config.property("jwt.secret_key").getString()
            }

            return TokenConfig(
                issuer = "http://0.0.0.0:8080",
                audience = "users",
                expiresIn = 365L * 1000L * 60L * 60L * 24L,
                secret = secret
            )
        }
    }
}
