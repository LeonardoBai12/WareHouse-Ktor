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
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.domain.model.WithdrawParameters
import io.lb.warehouse.withdraw.domain.model.WithdrawsSorting
import io.lb.warehouse.withdraw.domain.use_cases.WithdrawUseCases
import org.koin.ktor.ext.inject
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
 * Get withdraws list:
 * [/api/withdraws](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#a86ec3ff-a9ad-481f-8bcf-e5fc30da16a2)
 */
fun Application.withdrawRoutes() {
    val useCases by inject<WithdrawUseCases>()

    routing {
        authenticate {
            post("/api/createWithdraw") {
                val withdraw = call.receiveNullable<WithdrawCreateRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                try {
                    val id = useCases.createWithdrawUseCase(withdraw)
                    call.respond(HttpStatusCode.Created, id)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                } catch (e: WareHouseException) {
                    call.respond(e.code, e.message.toString())
                }
            }

            get("/api/withdraw") {
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val withdraw = useCases.getWithdrawByIDUseCase(id)
                    call.respond(HttpStatusCode.OK, withdraw)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                } catch (e: WareHouseException) {
                    call.respond(e.code, e.message.toString())
                }
            }

            get("/api/withdraws") {
                val sortBy = call.parameters["sortBy"] ?: WithdrawsSorting.BY_TIMESTAMP.label
                val order = call.parameters["order"] ?: WithdrawsSorting.SortOrder.ASCENDING.label
                val parameters = WithdrawParameters(
                    userId = call.parameters["userId"],
                    wareId = call.parameters["wareId"],
                    sortBy = sortBy,
                    order = order,
                )
                try {
                    val withdraw = useCases.getWithdrawsUseCase(parameters)
                    call.respond(HttpStatusCode.OK, withdraw)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                } catch (e: WareHouseException) {
                    call.respond(e.code, e.message.toString())
                }
            }
        }
    }
}
