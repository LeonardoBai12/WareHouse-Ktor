package io.lb.warehouse.withdraw.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService
import java.sql.Connection
import java.sql.SQLException

fun Application.withdrawRoutes(dbConnection: Connection) {
    val wareService = WithdrawDatabaseService(dbConnection)

    routing {
        post("/api/createWithdraw") {
            val withdraw = call.receiveNullable<WithdrawCreateRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            try {
                val id = wareService.insertWithdraw(withdraw)
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
            val ware = wareService.getWithdrawById(id) ?: run {
                call.respond(HttpStatusCode.NotFound, "There is no withdraws with such ID")
                return@get
            }
            call.respond(HttpStatusCode.OK, ware)
        }

        get("/api/withdrawsCreatedByUser") {
            try {
                val userId = call.parameters["userId"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val wares = wareService.getWithdrawsByUserId(userId)
                call.respond(HttpStatusCode.OK, wares)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound, "No withdraws for such user")
            }
        }

        get("/api/withdrawsByWareId") {
            try {
                val userId = call.parameters["wareId"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val wares = wareService.getWithdrawsByWareId(userId)
                call.respond(HttpStatusCode.OK, wares)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound, "No withdraws for such ware")
            }
        }
    }
}
