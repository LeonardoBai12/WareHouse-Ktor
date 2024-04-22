package io.lb.warehouse.ware.routes

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
import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.data.service.WareDatabaseService
import java.sql.Connection

fun Application.wareRoutes(dbConnection: Connection) {
    val wareService = WareDatabaseService(dbConnection)

    routing {
        post("/api/createWare") {
            val ware = call.receiveNullable<WareCreateRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val id = wareService.insertWare(
                ware
            )
            call.respond(HttpStatusCode.Created, id)
        }

        get("/api/ware") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val ware = wareService.getWareById(id) ?: run {
                call.respond(HttpStatusCode.NotFound, "There is no ware with such ID")
                return@get
            }
            call.respond(HttpStatusCode.OK, ware)
        }

        get("/api/waresCreatedByUser") {
            try {
                val userId = call.parameters["userId"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val wares = wareService.getWaresByUserId(userId)
                call.respond(HttpStatusCode.OK, wares)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound, "No wares for such user")
            }
        }

        put("/api/updateWare") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }

            wareService.getWareById(id) ?: run {
                call.respond(HttpStatusCode.NotFound, "There is no ware with such ID")
                return@put
            }

            val ware = call.receiveNullable<WareCreateRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }

            wareService.updateWare(id, ware)
            call.respond(HttpStatusCode.OK, id)
        }

        delete("/api/deleteWare") {
            val id = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            wareService.getWareById(id) ?: run {
                call.respond(HttpStatusCode.NotFound, "There is no ware with such ID")
                return@delete
            }

            wareService.deleteWare(id)
            call.respond(HttpStatusCode.OK, "Ware deleted successfully")
        }
    }
}
