package io.lb.warehouse.withdraw.data.service

import io.lb.warehouse.core.util.loadQueryFromFile
import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.data.model.WithdrawData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement
import java.util.UUID

class WithdrawDatabaseService(private val connection: Connection) {
    companion object {
        private const val CREATE_AFTER_WITHDRAW_TRIGGER = "create_after_withdraw_trigger.sql"
        private const val CREATE_UPDATE_QUANTITY_FUNCTION = "create_update_available_quantity_function.sql"
        private const val CREATE_TABLE_WITHDRAW = "create_table_withdraw.sql"
        private const val INSERT_WITHDRAW = "insert_withdraw.sql"
        private const val SELECT_WITHDRAW_BY_ID = "select_withdraw_by_id.sql"
        private const val SELECT_WITHDRAWS_BY_USER_ID = "select_withdraw_by_user_id.sql"
        private const val SELECT_WITHDRAWS_BY_WARE_ID = "select_withdraw_by_ware_id.sql"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(loadQueryFromFile(CREATE_TABLE_WITHDRAW))
        statement.executeUpdate(loadQueryFromFile(CREATE_UPDATE_QUANTITY_FUNCTION))
        statement.executeUpdate(loadQueryFromFile(CREATE_AFTER_WITHDRAW_TRIGGER))
    }

    suspend fun insertWithdraw(withdraw: WithdrawCreateRequest): String = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            loadQueryFromFile(INSERT_WITHDRAW),
            Statement.RETURN_GENERATED_KEYS
        )

        with(statement) {
            setObject(1, UUID.randomUUID())
            setObject(2, UUID.fromString(withdraw.userId))
            setObject(3, UUID.fromString(withdraw.wareId))
            setDouble(4, withdraw.quantity)
            executeUpdate()
        }

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getString(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted withdraw.")
        }
    }

    suspend fun getWithdrawById(id: String): WithdrawData? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            loadQueryFromFile(SELECT_WITHDRAW_BY_ID)
        )
        statement.setObject(1, UUID.fromString(id))
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val userId = resultSet.getString("user_id")
            val wareId = resultSet.getString("ware_id")
            val quantity = resultSet.getDouble("quantity")

            return@withContext WithdrawData(
                uuid = id,
                userId = userId,
                wareId = wareId,
                quantity = quantity,
            )
        } else {
            null
        }
    }

    suspend fun getWithdrawsByUserId(userUUID: String): List<WithdrawData> = withContext(Dispatchers.IO) {
        val withdraws = mutableListOf<WithdrawData>()
        val statement = connection.prepareStatement(
            loadQueryFromFile(SELECT_WITHDRAWS_BY_USER_ID)
        )
        statement.setObject(1, UUID.fromString(userUUID))
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            val id = resultSet.getString("uuid")
            val userId = resultSet.getString("user_id")
            val wareId = resultSet.getString("ware_id")
            val quantity = resultSet.getDouble("quantity")

            withdraws.add(
                WithdrawData(
                    uuid = id,
                    userId = userId,
                    wareId = wareId,
                    quantity = quantity,
                )
            )
        }

        return@withContext withdraws
    }

    suspend fun getWithdrawsByWareId(wareUUID: String): List<WithdrawData> = withContext(Dispatchers.IO) {
        val withdraws = mutableListOf<WithdrawData>()
        val statement = connection.prepareStatement(
            loadQueryFromFile(SELECT_WITHDRAWS_BY_WARE_ID)
        )
        statement.setObject(1, UUID.fromString(wareUUID))
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            val id = resultSet.getString("uuid")
            val userId = resultSet.getString("user_id")
            val wareId = resultSet.getString("ware_id")
            val quantity = resultSet.getDouble("quantity")

            withdraws.add(
                WithdrawData(
                    uuid = id,
                    userId = userId,
                    wareId = wareId,
                    quantity = quantity
                )
            )
        }

        return@withContext withdraws
    }
}
