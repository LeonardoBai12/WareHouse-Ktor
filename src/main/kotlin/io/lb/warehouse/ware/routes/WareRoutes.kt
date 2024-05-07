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
import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.data.model.WareData
import io.lb.warehouse.ware.data.model.WareSorting
import io.lb.warehouse.ware.data.service.WareDatabaseService
import java.sql.SQLException

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
 *
 * @param wareService Service class for interacting with the ware table in the PostgreSQL database.
 */
fun Application.wareRoutes(wareService: WareDatabaseService) {
    routing {
        authenticate {
            post("/api/createWare") {
                val ware = call.receiveNullable<WareCreateRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                try {
                    val id = wareService.insertWare(ware)
                    call.respond(HttpStatusCode.Created, id)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                    return@post
                }
            }

            get("/api/ware") {
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val ware = wareService.getWareById(id) ?: run {
                    call.respond(HttpStatusCode.NotFound, "There is no wares with such ID")
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
                    val result = wareService.getWaresByUserId(userId)

                    if (result.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound, "There is no wares for such user")
                        return@get
                    }

                    val sortBy = call.parameters["sortBy"] ?: WareSorting.BY_TIMESTAMP.label
                    val order = call.parameters["order"] ?: WareSorting.SortOrder.ASCENDING.label

                    val wares = try {
                        result.getOrderedWares(sortBy, order)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, e.message.toString())
                        return@get
                    }

                    call.respond(HttpStatusCode.OK, wares)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound, "There is no wares for such user")
                }
            }

            put("/api/updateWare") {
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }

                wareService.getWareById(id) ?: run {
                    call.respond(HttpStatusCode.NotFound, "There is no wares with such ID")
                    return@put
                }

                val ware = call.receiveNullable<WareCreateRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }

                try {
                    wareService.updateWare(id, ware)
                    call.respond(HttpStatusCode.OK, id)
                } catch (e: SQLException) {
                    call.respond(HttpStatusCode.Forbidden, e.message.toString())
                    return@put
                }
            }

            delete("/api/deleteWare") {
                val id = call.parameters["id"] ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                wareService.getWareById(id) ?: run {
                    call.respond(HttpStatusCode.NotFound, "There is no wares with such ID")
                    return@delete
                }

                wareService.deleteWare(id)
                call.respond(HttpStatusCode.OK, "Ware deleted successfully")
            }
        }
    }
}

private fun List<WareData>.getOrderedWares(
    sortBy: String,
    order: String,
): List<WareData> {
    return when (order) {
        WareSorting.SortOrder.ASCENDING.label -> {
            sortedBy {
                it.getSortingTypeByLabel(sortBy)
            }
        }

        WareSorting.SortOrder.DESCENDING.label -> {
            sortedByDescending {
                it.getSortingTypeByLabel(sortBy)
            }
        }

        else -> {
            throw Exception("Order should be: [asc, desc]")
        }
    }
}

private fun WareData.getSortingTypeByLabel(sortBy: String) = when (sortBy) {
    WareSorting.BY_NAME.label -> {
        name
    }

    WareSorting.BY_BRAND.label -> {
        brand
    }

    WareSorting.BY_AVAILABLE_QUANTITY.label -> {
        availableQuantity.toString()
    }

    WareSorting.BY_TIMESTAMP.label -> {
        timestamp
    }

    else -> {
        throw Exception("Sorting should be: [name, brand, quantity, timestamp]")
    }
}
