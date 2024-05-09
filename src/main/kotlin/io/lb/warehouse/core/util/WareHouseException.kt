package io.lb.warehouse.core.util

import io.ktor.http.HttpStatusCode

data class WareHouseException(
    val code: HttpStatusCode,
    override val message: String?
) : Exception()
