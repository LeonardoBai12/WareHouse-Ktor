package io.lb.warehouse.ware.data.service

import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.data.model.WareData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class WareDatabaseService(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE_WARE =
            "CREATE TABLE IF NOT EXISTS ware ( " +
                "     uuid UUID PRIMARY KEY, " +
                "     user_id UUID REFERENCES user_data(user_id) ON DELETE CASCADE, " +
                "     title VARCHAR(255) NOT NULL, " +
                "     description TEXT, " +
                "     ware_type VARCHAR(50) NOT NULL, " +
                "     deadline_date DATE, " +
                "     deadline_time TIME, " +
                "     timestamp TIMESTAMPTZ DEFAULT NOW() NOT NULL " +
                ");"
        private const val SELECT_WARES_BY_USER_ID =
            "SELECT uuid, title, user_id, description, ware_type, deadline_date, deadline_time " +
                "FROM ware " +
                "WHERE user_id = ?;"
        private const val SELECT_WARE_BY_ID =
            "SELECT title, user_id, description, ware_type, deadline_date, deadline_time " +
                "FROM ware " +
                "WHERE uuid = ?;"
        private const val INSERT_WARE =
            "INSERT INTO ware (uuid, title, user_id, description, ware_type, deadline_date, deadline_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);"
        private const val UPDATE_WARE =
            "UPDATE ware SET " +
                "    title = ?, " +
                "    description = ?, " +
                "    deadline_date = ?, " +
                "    deadline_time = ? " +
                "WHERE uuid = ?;"
        private const val DELETE_WARE = "DELETE FROM ware WHERE uuid = ?;"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_WARE)
    }

    suspend fun insertWare(ware: WareCreateRequest): String = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_WARE, Statement.RETURN_GENERATED_KEYS)

        with(statement) {
            setObject(1, UUID.randomUUID())
            setString(2, ware.title)
            setObject(3, UUID.fromString(ware.userId))
            setString(4, ware.description)
            setString(5, ware.wareType)
            setObject(6, LocalDate.parse(ware.deadlineDate))
            setObject(7, LocalTime.parse(ware.deadlineTime))
            executeUpdate()
        }

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getString(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted ware.")
        }
    }

    suspend fun getWareById(id: String): WareData? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_WARE_BY_ID)
        statement.setObject(1, UUID.fromString(id))
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val title = resultSet.getString("title")
            val userId = resultSet.getString("user_id")
            val description = resultSet.getString("description")
            val wareType = resultSet.getString("ware_type")
            val deadlineDate = resultSet.getString("deadline_date")
            val deadlineTime = resultSet.getString("deadline_time")
            return@withContext WareData(
                uuid = id,
                title = title,
                userId = userId,
                description = description,
                wareType = wareType,
                deadlineDate = deadlineDate,
                deadlineTime = deadlineTime,
            )
        } else {
            null
        }
    }

    suspend fun getWaresByUserId(userUUID: String): List<WareData> = withContext(Dispatchers.IO) {
        val wares = mutableListOf<WareData>()
        val statement = connection.prepareStatement(SELECT_WARES_BY_USER_ID)
        statement.setObject(1, UUID.fromString(userUUID))
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            val id = resultSet.getString("uuid")
            val title = resultSet.getString("title")
            val userId = resultSet.getString("user_id")
            val description = resultSet.getString("description")
            val wareType = resultSet.getString("ware_type")
            val deadlineDate = resultSet.getString("deadline_date")
            val deadlineTime = resultSet.getString("deadline_time")

            wares.add(
                WareData(
                    uuid = id,
                    title = title,
                    userId = userId,
                    description = description,
                    wareType = wareType,
                    deadlineDate = deadlineDate,
                    deadlineTime = deadlineTime,
                )
            )
        }

        return@withContext wares
    }

    suspend fun updateWare(id: String, ware: WareCreateRequest) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(UPDATE_WARE)) {
            setString(1, ware.title)
            setString(2, ware.description)
            setObject(3, LocalDate.parse(ware.deadlineDate))
            setObject(4, LocalTime.parse(ware.deadlineTime))
            setObject(5, UUID.fromString(id))
            executeUpdate()
        }
    }

    suspend fun deleteWare(id: String) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(DELETE_WARE)) {
            setObject(1, UUID.fromString(id))
            executeUpdate()
        }
    }
}
