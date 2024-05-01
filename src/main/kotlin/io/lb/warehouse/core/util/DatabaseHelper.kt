package io.lb.warehouse.core.util

import java.io.File

fun loadQueryFromFile(fileName: String): String {
    val projectDir = System.getProperty("user.dir")
    val sqlDir = File("$projectDir/src/main/sql")
    val file = File(sqlDir, fileName)

    return file.bufferedReader().use { it.readText() }
}
