package io.lb.warehouse.user.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.util.generateNonce
import io.ktor.util.pipeline.PipelineContext
import io.lb.warehouse.core.extensions.encrypt
import io.lb.warehouse.core.extensions.passwordCheck
import io.lb.warehouse.core.session.WarehouseSession
import io.lb.warehouse.security.data.model.TokenClaim
import io.lb.warehouse.security.data.model.TokenConfig
import io.lb.warehouse.security.generateToken
import io.lb.warehouse.user.data.model.ProtectedUserRequest
import io.lb.warehouse.user.data.model.UpdatePasswordRequest
import io.lb.warehouse.user.data.model.UserCreateRequest
import io.lb.warehouse.user.data.model.UserData
import io.lb.warehouse.user.data.model.UserUpdateRequest
import io.lb.warehouse.user.data.service.UserDatabaseService

/**
 * Extension function with routes related to user operations.
 *
 * **Routes documentations:**
 *
 * Sign up (Create user):
 * [/api/signIn](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#a61f2df0-1f61-4537-b458-28b755bc9a75)
 *
 * Login (Get auth token):
 * [/api/login](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#bea84de4-64d3-45c3-a2cc-35deef980dc7)
 *
 * Logout:
 * [/api/login](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#dc56397a-756f-4b97-b4db-a7fad39499a8)
 *
 * Get user by UUID:
 * [/api/user](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#e836f3e1-130e-48e1-8045-69d8e2c8f2b8)
 *
 * Update user:
 * [/api/updateUser](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#f4555925-1043-4457-840a-b0b1cd62efd9)
 *
 * Update user's password:
 * [/api/updatePassword](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#df877090-8a69-4823-8bfa-a570f74231c3)
 *
 * Delete user:
 * [/api/deleteUser](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#44e9c25d-ef79-446b-8dca-be4934599061)
 *
 * @param tokenConfig Data class representing token configurations.
 * @param userService Service class for interacting with the user data table in the PostgreSQL database.
 */
fun Application.userRoutes(
    tokenConfig: TokenConfig,
    userService: UserDatabaseService
) {
    routing {
        post("/api/signUp") {
            val user = call.receiveNullable<UserCreateRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            call.sessions.get<WarehouseSession>()?.let {
                call.respond(HttpStatusCode.Conflict, "There is already an user logged in.")
                return@post
            }

            if (!validateEmail(userService, user.email)) {
                return@post
            }

            if (user.userName.isBlank()) {
                call.respond(HttpStatusCode.Conflict, "User must have a name.")
                return@post
            }

            if (user.password.length < 8) {
                call.respond(HttpStatusCode.Conflict, "Password must have more than 8 characters.")
                return@post
            }

            val hashedPassword = user.password.encrypt()
            val userData = UserData(
                userName = user.userName,
                password = hashedPassword,
                email = user.email,
                profilePictureUrl = user.profilePictureUrl,
            )
            userService.createUser(userData)
            call.respond(HttpStatusCode.Created, userData.userId)
        }

        get("/api/login") {
            val userId = call.parameters["userId"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            call.sessions.get<WarehouseSession>()?.let {
                call.respond(HttpStatusCode.Conflict, "There is already an user logged in.")
                return@get
            }

            val request = call.receiveNullable<ProtectedUserRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            validatePassword(
                userService = userService,
                userId = userId,
                password = request.password,
            ) {
                val token = generateToken(
                    config = tokenConfig,
                    TokenClaim(
                        name = "userId",
                        value = userId
                    )
                )

                call.sessions.set(
                    WarehouseSession(
                        clientId = userId,
                        sessionId = generateNonce()
                    )
                )

                call.respond(HttpStatusCode.OK, token)
            }
        }

        authenticate {
            get("/api/user") {
                val userId = call.parameters["userId"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                userService.getUserById(userId)?.let {
                    call.respond(HttpStatusCode.OK, it.copy(password = null))
                } ?: call.respond(HttpStatusCode.NotFound, "There is no user with such ID")
            }

            put("/api/updateUser") {
                val userId = call.parameters["userId"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }

                if (!validateSession(userId)) {
                    return@put
                }

                val user = call.receiveNullable<UserUpdateRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }

                if (user.userName != null && user.userName.isBlank()) {
                    call.respond(HttpStatusCode.Conflict, "User must have a name.")
                    return@put
                }

                if (!validateEmail(userService, user.email)) {
                    return@put
                }

                validatePassword(
                    userService = userService,
                    userId = userId,
                    password = user.password,
                ) {
                    val updatedUser = it.copy(
                        userName = user.userName ?: it.userName,
                        email = user.email ?: it.email,
                        profilePictureUrl = user.profilePictureUrl ?: it.profilePictureUrl,
                    )
                    userService.updateUser(updatedUser)
                }
            }

            put("/api/updatePassword") {
                val userId = call.parameters["userId"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }

                if (!validateSession(userId)) {
                    return@put
                }

                val request = call.receiveNullable<UpdatePasswordRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }

                if (request.newPassword.length < 8) {
                    call.respond(HttpStatusCode.Conflict, "Password must have more than 8 characters.")
                    return@put
                }

                validatePassword(
                    userService = userService,
                    userId = userId,
                    password = request.password,
                ) {
                    userService.updatePassword(userId, request.newPassword.encrypt()!!)
                }
            }

            delete("/api/deleteUser") {
                val userId = call.parameters["userId"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                if (!validateSession(userId)) {
                    return@delete
                }

                val request = call.receiveNullable<ProtectedUserRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                validatePassword(
                    userService = userService,
                    userId = userId,
                    password = request.password,
                ) {
                    userService.deleteUser(userId)
                }
            }

            get("/api/logout") {
                call.sessions.clear<WarehouseSession>()
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

private suspend fun PipelineContext<*, ApplicationCall>.validatePassword(
    userService: UserDatabaseService,
    userId: String,
    password: String,
    block: suspend (UserData) -> Unit
) {
    val storedUser = userService.getUserById(userId) ?: run {
        call.respond(HttpStatusCode.NotFound, "There is no user with such ID")
        return
    }

    password.takeIf { it.isEmpty() }?.let {
        call.respond(HttpStatusCode.Unauthorized, "Invalid password")
        return
    }

    storedUser.takeIf {
        password.passwordCheck(it.password!!)
    }?.let {
        block.invoke(storedUser)
        call.respond(HttpStatusCode.OK, userId)
    } ?: call.respond(HttpStatusCode.Unauthorized, "Invalid password")
}

private suspend fun PipelineContext<*, ApplicationCall>.validateSession(userId: String): Boolean {
    val authenticatedUserId = call.sessions.get<WarehouseSession>()?.clientId

    if (userId != authenticatedUserId) {
        call.respond(HttpStatusCode.Unauthorized, "You are not authorized to update this user.")
        return false
    }

    return true
}

private suspend fun PipelineContext<*, ApplicationCall>.validateEmail(
    userService: UserDatabaseService,
    email: String?
): Boolean {
    if (email != null && userService.isEmailAlreadyInUse(email)) {
        call.respond(HttpStatusCode.Conflict, "Email already in use by another user.")
        return false
    }

    return true
}
