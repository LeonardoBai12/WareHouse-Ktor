package io.lb.warehouse.deposit.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.data.service.DepositDatabaseService
import java.sql.SQLException

fun Application.depositRoutes(depositService: DepositDatabaseService) {
    routing {
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
                call.respond(HttpStatusCode.OK, deposits)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound, "No deposits for such user")
            }
        }

        get("/api/depositsByWareId") {
            try {
                val userId = call.parameters["wareId"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val deposits = depositService.getDepositsByWareId(userId)
                call.respond(HttpStatusCode.OK, deposits)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound, "No deposits for such ware")
            }
        }
    }
}
