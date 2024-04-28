package io.lb.warehouse.core.util

import jdk.jfr.internal.SecuritySupport

fun loadQueryFromFile(fileName: String): String? {
    val inputStream = SecuritySupport.getResourceAsStream("/$fileName")
    return inputStream?.bufferedReader().use { it?.readText() }
}
