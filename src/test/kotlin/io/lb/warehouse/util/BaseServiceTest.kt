package io.lb.warehouse.util

import io.lb.warehouse.core.util.loadQueryFromFile
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class BaseServiceTest(private val createQueryFile: String) {
    val connection: Connection = mockk()
    val queryResult: ResultSet = mockk()
    val preparedStatement: PreparedStatement = mockk(relaxed = true)
    val statement: Statement = mockk(relaxed = true)

    @BeforeEach
    open fun setUp() {
        val query = loadQueryFromFile(createQueryFile)

        setUpPreparedStatement(preparedStatement, queryResult)

        every { connection.createStatement() } returns statement

        every {
            statement.executeUpdate(query)
        } returns 1
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    private fun setUpPreparedStatement(statement: PreparedStatement, queryResult: ResultSet) {
        with(statement) {
            every { setObject(any(), any()) } just runs
            every { setString(any(), any()) } just runs
            every { setDouble(any(), any()) } just runs

            every { executeQuery() } returns queryResult
            every { executeUpdate() } returns 1
        }
    }
}
