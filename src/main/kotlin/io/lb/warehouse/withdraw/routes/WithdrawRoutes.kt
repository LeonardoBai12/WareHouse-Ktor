package io.lb.warehouse.withdraw.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseServiceImpl
import java.sql.SQLException

/**
 * Extension function with routes related to withdraw operations.
 *
 * **Routes documentations:**
 *
 * Create withdraw:
 * [/api/createWithdraw](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#766c06d9-119c-4a7c-918a-d46bcb0fbd73)
 *
 * Get withdraw by UUID:
 * [/api/withdraw](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#c65cd374-3703-4d2a-8e6c-28a46a7fc9c5)
 *
 * Get withdraws by user UUID:
 * [/api/withdrawsCreatedByUser](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#a86ec3ff-a9ad-481f-8bcf-e5fc30da16a2)
 *
 * Get withdraws by ware UUID:
 * [/api/withdrawsByWareId](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#c9a18637-a85d-42d3-88c7-4e620065b552)
 *
 * @param withdrawService Service class for interacting with the withdrawal table in the PostgreSQL database.
 */
fun Application.withdrawRoutes(withdrawService: WithdrawDatabaseServiceImpl) {
    routing {
        authenticate {
            post("/api/createWithdraw") {
                val withdraw = call.receiveNullable<WithdrawCreateRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                try {
                    val id = withdrawService.insertWithdraw(withdraw)
                    call.respond(HttpStatusCode.Created, id)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.localizedMessage)
                    return@post
                }
            }

            get("/api/withdraw") {
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val withdraw = withdrawService.getWithdrawById(id) ?: run {
                    call.respond(HttpStatusCode.NotFound, "There is no withdraws with such ID")
                    return@get
                }
                call.respond(HttpStatusCode.OK, withdraw)
            }

            get("/api/withdrawsCreatedByUser") {
                try {
                    val userId = call.parameters["userId"] ?: run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }
                    val withdraws = withdrawService.getWithdrawsByUserId(userId)

                    if (withdraws.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound, "There is no withdraws for such user")
                        return@get
                    }

                    call.respond(HttpStatusCode.OK, withdraws)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound, "There is no withdraws for such user")
                }
            }

            get("/api/withdrawsByWareId") {
                try {
                    val wareId = call.parameters["wareId"] ?: run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }
                    val withdraws = withdrawService.getWithdrawsByWareId(wareId)

                    if (withdraws.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound, "There is no withdraws for such ware")
                        return@get
                    }

                    call.respond(HttpStatusCode.OK, withdraws)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound, "There is no withdraws for such ware")
                }
            }
        }
    }
}
