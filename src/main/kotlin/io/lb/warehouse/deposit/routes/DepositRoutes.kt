package io.lb.warehouse.deposit.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.data.service.DepositDatabaseService
import java.sql.SQLException

/**
 * Extension function with routes related to deposit operations.
 *
 * **Routes documentations:**
 *
 * Create deposit:
 * [/api/createDeposit](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#d5fd3458-142b-41da-90b0-9425eb7f1157)
 *
 * Get deposit by UUID:
 * [/api/deposit](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#105e8197-f646-440a-9eff-40bce2c3721d)
 *
 * Get deposits by user UUID:
 * [/api/depositsCreatedByUser](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#0c6485b0-c820-4bc8-99f2-2c4f1b33389b)
 *
 * Get deposits by ware UUID:
 * [/api/depositsByWareId](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#03bbb3f7-1a9d-452d-965c-388e73a4eb59)
 *
 * @param depositService Service class for interacting with the deposit table in the PostgreSQL database.
 */
fun Application.depositRoutes(depositService: DepositDatabaseService) {
    routing {
        authenticate {
            post("/api/createDeposit") {
                val deposit = call.receiveNullable<DepositCreateRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                try {
                    val id = depositService.insertDeposit(deposit)
                    call.respond(HttpStatusCode.Created, id)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.localizedMessage)
                    return@post
                }
            }

            get("/api/deposit") {
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val deposit = depositService.getDepositById(id) ?: run {
                    call.respond(HttpStatusCode.NotFound, "There is no deposits with such ID")
                    return@get
                }
                call.respond(HttpStatusCode.OK, deposit)
            }

            get("/api/depositsCreatedByUser") {
                try {
                    val userId = call.parameters["userId"] ?: run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }
                    val deposits = depositService.getDepositsByUserId(userId)

                    if (deposits.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound, "There is no deposits for such user")
                        return@get
                    }

                    call.respond(HttpStatusCode.OK, deposits)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound, "There is no deposits for such user")
                }
            }

            get("/api/depositsByWareId") {
                try {
                    val wareId = call.parameters["wareId"] ?: run {
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }
                    val deposits = depositService.getDepositsByWareId(wareId)

                    if (deposits.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound, "There is no deposits for such ware")
                        return@get
                    }

                    call.respond(HttpStatusCode.OK, deposits)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound, "There is no deposits for such ware")
                }
            }
        }
    }
}
