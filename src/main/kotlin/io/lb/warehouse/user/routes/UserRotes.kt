package io.lb.warehouse.user.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.lb.warehouse.core.extensions.encrypt
import io.lb.warehouse.core.extensions.passwordCheck
import io.lb.warehouse.user.data.model.DeleteUserRequest
import io.lb.warehouse.user.data.model.UpdatePasswordRequest
import io.lb.warehouse.user.data.model.UserCreateRequest
import io.lb.warehouse.user.data.model.UserData
import io.lb.warehouse.user.data.service.UserDatabaseService

/**
 * Extension function with routes related to user operations.
 *
 * **Routes documentations:**
 *
 * Create user:
 * [/api/createUser](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#a61f2df0-1f61-4537-b458-28b755bc9a75)
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
 */
fun Application.userRoutes(userService: UserDatabaseService) {
    routing {
        post("/api/createUser") {
            val user = call.receiveNullable<UserCreateRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            if (userService.isEmailAlreadyInUse(user.email)) {
                call.respond(HttpStatusCode.Conflict, "Email already in use by another user.")
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

            val storedUser = userService.getUserById(userId) ?: run {
                call.respond(HttpStatusCode.NotFound, "There is no user with such ID")
                return@put
            }

            val user = call.receiveNullable<UserCreateRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }

            if (userService.isEmailAlreadyInUse(user.email)) {
                call.respond(HttpStatusCode.Conflict, "Email already in use by another user.")
                return@put
            }

            user.password.takeIf { it.isEmpty() }?.let {
                call.respond(HttpStatusCode.Unauthorized, "Invalid password")
                return@put
            }

            storedUser.takeIf {
                user.password.passwordCheck(it.password!!)
            }?.let {
                val updatedUser = it.copy(
                    userName = user.userName,
                    email = user.email,
                    profilePictureUrl = user.profilePictureUrl,
                )
                userService.updateUser(updatedUser)
                call.respond(HttpStatusCode.OK, userId)
            } ?: call.respond(HttpStatusCode.Unauthorized, "Invalid password")
        }

        put("/api/updatePassword") {
            val userId = call.parameters["userId"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }

            val storedUser = userService.getUserById(userId) ?: run {
                call.respond(HttpStatusCode.NotFound, "There is no user with such ID")
                return@put
            }

            val request = call.receiveNullable<UpdatePasswordRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }

            request.password.takeIf { it.isEmpty() }?.let {
                call.respond(HttpStatusCode.Unauthorized, "Invalid password")
                return@put
            }

            storedUser.takeIf {
                request.password.passwordCheck(it.password!!)
            }?.let {
                userService.updatePassword(userId, request.newPassword.encrypt()!!)
                call.respond(HttpStatusCode.OK, userId)
            } ?: call.respond(HttpStatusCode.Unauthorized, "Invalid password")
        }

        delete("/api/deleteUser") {
            val userId = call.parameters["userId"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val storedUser = userService.getUserById(userId) ?: run {
                call.respond(HttpStatusCode.NotFound, "There is no user with such ID")
                return@delete
            }

            val request = call.receiveNullable<DeleteUserRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            request.password.takeIf { it.isEmpty() }?.let {
                call.respond(HttpStatusCode.Unauthorized, "Invalid password")
                return@delete
            }

            storedUser.takeIf {
                request.password.passwordCheck(it.password!!)
            }?.let {
                userService.deleteUser(userId)
                call.respond(HttpStatusCode.OK, userId)
            } ?: call.respond(HttpStatusCode.Unauthorized, "Invalid password")
        }
    }
}
