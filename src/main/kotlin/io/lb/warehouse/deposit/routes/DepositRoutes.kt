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
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.domain.use_cases.DepositUseCases
import java.sql.SQLException
import org.koin.ktor.ext.inject

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
 */
fun Application.depositRoutes() {
    val useCases by inject<DepositUseCases>()

    routing {
        authenticate {
            post("/api/createDeposit") {
                val deposit = call.receiveNullable<DepositCreateRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                try {
                    val id = useCases.createDepositUseCase(deposit)
                    call.respond(HttpStatusCode.Created, id)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                } catch (e: WareHouseException) {
                    call.respond(e.code, e.message.toString())
                }
            }

            get("/api/deposit") {
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val deposit = useCases.getDepositByIDUseCase(id)
                    call.respond(HttpStatusCode.OK, deposit)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                } catch (e: WareHouseException) {
                    call.respond(e.code, e.message.toString())
                }
            }

            get("/api/depositsCreatedByUser") {
                val userId = call.parameters["userId"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val deposits = useCases.getDepositsByUserIdUseCase(userId)
                    call.respond(HttpStatusCode.OK, deposits)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                } catch (e: WareHouseException) {
                    call.respond(e.code, e.message.toString())
                }
            }

            get("/api/depositsByWareId") {
                val userId = call.parameters["wareId"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val deposit = useCases.getDepositsByWareIdUseCase(userId)
                    call.respond(HttpStatusCode.OK, deposit)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                } catch (e: WareHouseException) {
                    call.respond(e.code, e.message.toString())
                }
            }
        }
    }
}
