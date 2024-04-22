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
        private const val CREATE_TABLE_TASk =
            "CREATE TABLE IF NOT EXISTS task ( " +
                "     uuid UUID PRIMARY KEY, " +
                "     user_id UUID REFERENCES user_data(user_id) ON DELETE CASCADE, " +
                "     title VARCHAR(255) NOT NULL, " +
                "     description TEXT, " +
                "     task_type VARCHAR(50) NOT NULL, " +
                "     deadline_date DATE, " +
                "     deadline_time TIME, " +
                "     timestamp TIMESTAMPTZ DEFAULT NOW() NOT NULL " +
                ");"
        private const val SELECT_TASKS_BY_USER_ID =
            "SELECT uuid, title, user_id, description, task_type, deadline_date, deadline_time " +
                "FROM task " +
                "WHERE user_id = ?;"
        private const val SELECT_TASK_BY_ID =
            "SELECT title, user_id, description, task_type, deadline_date, deadline_time " +
                "FROM task " +
                "WHERE uuid = ?;"
        private const val INSERT_TASK =
            "INSERT INTO task (uuid, title, user_id, description, task_type, deadline_date, deadline_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);"
        private const val UPDATE_TASK =
            "UPDATE task SET " +
                "    title = ?, " +
                "    description = ?, " +
                "    deadline_date = ?, " +
                "    deadline_time = ? " +
                "WHERE uuid = ?;"
        private const val DELETE_TASK = "DELETE FROM task WHERE uuid = ?;"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_TASk)
    }

    suspend fun insertTask(task: WareCreateRequest): String = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_TASK, Statement.RETURN_GENERATED_KEYS)

        with(statement) {
            setObject(1, UUID.randomUUID())
            setString(2, task.title)
            setObject(3, UUID.fromString(task.userId))
            setString(4, task.description)
            setString(5, task.taskType)
            setObject(6, LocalDate.parse(task.deadlineDate))
            setObject(7, LocalTime.parse(task.deadlineTime))
            executeUpdate()
        }

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getString(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted task.")
        }
    }

    suspend fun getTaskById(id: String): WareData? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_TASK_BY_ID)
        statement.setObject(1, UUID.fromString(id))
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val title = resultSet.getString("title")
            val userId = resultSet.getString("user_id")
            val description = resultSet.getString("description")
            val taskType = resultSet.getString("task_type")
            val deadlineDate = resultSet.getString("deadline_date")
            val deadlineTime = resultSet.getString("deadline_time")
            return@withContext WareData(
                uuid = id,
                title = title,
                userId = userId,
                description = description,
                taskType = taskType,
                deadlineDate = deadlineDate,
                deadlineTime = deadlineTime,
            )
        } else {
            null
        }
    }

    suspend fun getTasksByUserId(userUUID: String): List<WareData> = withContext(Dispatchers.IO) {
        val tasks = mutableListOf<WareData>()
        val statement = connection.prepareStatement(SELECT_TASKS_BY_USER_ID)
        statement.setObject(1, UUID.fromString(userUUID))
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            val id = resultSet.getString("uuid")
            val title = resultSet.getString("title")
            val userId = resultSet.getString("user_id")
            val description = resultSet.getString("description")
            val taskType = resultSet.getString("task_type")
            val deadlineDate = resultSet.getString("deadline_date")
            val deadlineTime = resultSet.getString("deadline_time")

            tasks.add(
                WareData(
                    uuid = id,
                    title = title,
                    userId = userId,
                    description = description,
                    taskType = taskType,
                    deadlineDate = deadlineDate,
                    deadlineTime = deadlineTime,
                )
            )
        }

        return@withContext tasks
    }

    suspend fun updateTask(id: String, task: WareCreateRequest) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(UPDATE_TASK)) {
            setString(1, task.title)
            setString(2, task.description)
            setObject(3, LocalDate.parse(task.deadlineDate))
            setObject(4, LocalTime.parse(task.deadlineTime))
            setObject(5, UUID.fromString(id))
            executeUpdate()
        }
    }

    suspend fun deleteTask(id: String) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(DELETE_TASK)) {
            setObject(1, UUID.fromString(id))
            executeUpdate()
        }
    }
}
