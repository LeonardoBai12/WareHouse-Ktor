package io.lb.warehouse.withdraw.data.service

import io.lb.warehouse.core.util.loadQueryFromFile
import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.data.model.WithdrawData
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService.Companion.CREATE_AFTER_WITHDRAW_TRIGGER
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService.Companion.CREATE_TABLE_WITHDRAW
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService.Companion.CREATE_UPDATE_QUANTITY_FUNCTION
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService.Companion.INSERT_WITHDRAW
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService.Companion.SELECT_WITHDRAWS
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService.Companion.SELECT_WITHDRAW_BY_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement
import java.util.UUID

/**
 * Service class for interacting with the withdrawal table in the PostgreSQL database.
 *
 * @property connection Connection to the database.
 */
class WithdrawDatabaseServiceImpl(private val connection: Connection) : WithdrawDatabaseService {
    init {
        val statement = connection.createStatement()
        statement.executeUpdate(loadQueryFromFile(CREATE_TABLE_WITHDRAW))
        statement.executeUpdate(loadQueryFromFile(CREATE_UPDATE_QUANTITY_FUNCTION))
        statement.executeUpdate(loadQueryFromFile(CREATE_AFTER_WITHDRAW_TRIGGER))
    }

    override suspend fun insertWithdraw(withdraw: WithdrawCreateRequest): String = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            loadQueryFromFile(INSERT_WITHDRAW),
            Statement.RETURN_GENERATED_KEYS
        )
        val uuid = UUID.randomUUID()

        with(statement) {
            setObject(1, uuid)
            setObject(2, UUID.fromString(withdraw.userId))
            setObject(3, UUID.fromString(withdraw.wareId))
            setDouble(4, withdraw.quantity)

            executeUpdate()
        }

        uuid.toString()
    }

    override suspend fun getWithdrawById(id: String): WithdrawData? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            loadQueryFromFile(SELECT_WITHDRAW_BY_ID)
        )
        statement.setObject(1, UUID.fromString(id))
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val userId = resultSet.getString("user_id")
            val wareId = resultSet.getString("ware_id")
            val quantity = resultSet.getDouble("quantity")
            val timestamp = resultSet.getString("timestamp")

            return@withContext WithdrawData(
                uuid = id,
                userId = userId,
                wareId = wareId,
                quantity = quantity,
                timestamp = timestamp,
            )
        } else {
            null
        }
    }

    override suspend fun getWithdraws(
        userUUID: String?,
        wareUUID: String?
    ): List<WithdrawData> = withContext(Dispatchers.IO) {
        val withdraws = mutableListOf<WithdrawData>()
        val statement = connection.prepareStatement(
            loadQueryFromFile(SELECT_WITHDRAWS)
        )
        val userUUIDFilter = userUUID?.let {
            UUID.fromString(userUUID)
        }
        val wareUUIDFilter = wareUUID?.let {
            UUID.fromString(wareUUID)
        }
        statement.setObject(1, userUUIDFilter)
        statement.setObject(2, wareUUIDFilter)
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            val id = resultSet.getString("uuid")
            val userId = resultSet.getString("user_id")
            val wareId = resultSet.getString("ware_id")
            val quantity = resultSet.getDouble("quantity")
            val timestamp = resultSet.getString("timestamp")

            withdraws.add(
                WithdrawData(
                    uuid = id,
                    userId = userId,
                    wareId = wareId,
                    quantity = quantity,
                    timestamp = timestamp,
                )
            )
        }

        return@withContext withdraws
    }
}
