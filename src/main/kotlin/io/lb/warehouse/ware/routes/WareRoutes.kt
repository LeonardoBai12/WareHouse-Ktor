package io.lb.warehouse.ware.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.lb.warehouse.core.util.WareHouseException
import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.data.model.WareSorting
import io.lb.warehouse.ware.domain.use_cases.WareUseCases
import java.sql.SQLException
import org.koin.ktor.ext.inject

/**
 * Extension function with routes related to ware operations.
 *
 * **Routes documentations:**
 *
 * Create ware:
 * [/api/createWare](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#3ac9f51b-ceaf-486c-9a96-b99639dbe7c1)
 *
 * Update ware:
 * [/api/updateWare](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#f2f873df-c270-436f-829d-18abb382de33)
 *
 * Get ware by UUID:
 * [/api/ware](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#5fd92bd5-c595-4964-9563-3b6ed6c14bca)
 *
 * Get wares by user UUID:
 * [/api/waresCreatedByUser](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#d26339cb-c8f0-49d2-a87d-c75abbd07efa)
 *
 * Delete ware:
 * [/api/deleteWare](https://documenter.getpostman.com/view/28162587/2sA3JGeihC#0e7b55ba-0ade-4e5b-bcb5-7ae7ea665204)
 */
fun Application.wareRoutes() {
    val useCases by inject<WareUseCases>()

    routing {
        authenticate {
            post("/api/createWare") {
                val ware = call.receiveNullable<WareCreateRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                try {
                    val id = useCases.createWareUseCase(ware)
                    call.respond(HttpStatusCode.Created, id)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                } catch (e: WareHouseException) {
                    call.respond(e.code, e.message.toString())
                }
            }

            get("/api/ware") {
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                try {
                    val ware = useCases.getWareByIdUseCase(id)
                    call.respond(HttpStatusCode.OK, ware)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                } catch (e: WareHouseException) {
                    call.respond(e.code, e.message.toString())
                }
            }

            get("/api/waresCreatedByUser") {
                val userId = call.parameters["userId"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val sortBy = call.parameters["sortBy"] ?: WareSorting.BY_TIMESTAMP.label
                val order = call.parameters["order"] ?: WareSorting.SortOrder.ASCENDING.label

                try {
                    val wares = useCases.getWaresByUserIdUseCase(userId, sortBy, order)
                    call.respond(HttpStatusCode.OK, wares)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                } catch (e: WareHouseException) {
                    call.respond(e.code, e.message.toString())
                }
            }

            put("/api/updateWare") {
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }
                val ware = call.receiveNullable<WareCreateRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }

                try {
                    useCases.updateWareUseCase(id, ware)
                    call.respond(HttpStatusCode.OK, id)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                } catch (e: WareHouseException) {
                    call.respond(e.code, e.message.toString())
                }
            }

            delete("/api/deleteWare") {
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                try {
                    useCases.deleteWareUseCase(id)
                    call.respond(HttpStatusCode.OK, "Ware deleted successfully")
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                } catch (e: WareHouseException) {
                    call.respond(e.code, e.message.toString())
                }
            }
        }
    }
}
