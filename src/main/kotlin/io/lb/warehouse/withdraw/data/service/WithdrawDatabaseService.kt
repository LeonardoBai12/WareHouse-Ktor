package io.lb.warehouse.withdraw.data.service

import io.lb.warehouse.core.util.loadQueryFromFile
import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.data.model.WithdrawData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.VisibleForTesting
import java.sql.Connection
import java.sql.Statement
import java.util.UUID

/**
 * Service class for interacting with the withdrawal table in the PostgreSQL database.
 *
 * @property connection Connection to the database.
 */
class WithdrawDatabaseService(private val connection: Connection) {
    /**
     * @suppress
     */
    @VisibleForTesting
    companion object {
        const val CREATE_AFTER_WITHDRAW_TRIGGER = "withdraw/create_after_withdraw_trigger.sql"
        const val CREATE_UPDATE_QUANTITY_FUNCTION =
            "withdraw/create_update_available_quantity_function_on_withdraw.sql"
        const val CREATE_TABLE_WITHDRAW = "withdraw/create_table_withdraw.sql"
        const val INSERT_WITHDRAW = "withdraw/insert_withdraw.sql"
        const val SELECT_WITHDRAW_BY_ID = "withdraw/select_withdraw_by_id.sql"
        const val SELECT_WITHDRAWS_BY_USER_ID = "withdraw/select_withdraw_by_user_id.sql"
        const val SELECT_WITHDRAWS_BY_WARE_ID = "withdraw/select_withdraw_by_ware_id.sql"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(loadQueryFromFile(CREATE_TABLE_WITHDRAW))
        statement.executeUpdate(loadQueryFromFile(CREATE_UPDATE_QUANTITY_FUNCTION))
        statement.executeUpdate(loadQueryFromFile(CREATE_AFTER_WITHDRAW_TRIGGER))
    }

    /**
     * Inserts a withdrawal into the database.
     *
     * @param withdraw The withdrawal data to insert.
     * @return The UUID of the inserted withdrawal.
     */
    suspend fun insertWithdraw(withdraw: WithdrawCreateRequest): String = withContext(Dispatchers.IO) {
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

    /**
     * Retrieves a withdrawals by its ID from the database.
     *
     * @param id The ID of the withdrawals to retrieve.
     * @return The withdrawals data, or null if not found.
     */
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

    /**
     * Retrieves withdrawals by user ID from the database.
     *
     * @param userUUID The UUID of the user.
     * @return List of withdrawals associated with the user.
     */
    suspend fun getWithdrawsByUserId(userUUID: String): List<WithdrawData> = withContext(Dispatchers.IO) {
        val withdraws = mutableListOf<WithdrawData>()
        val statement = connection.prepareStatement(
            loadQueryFromFile(SELECT_WITHDRAWS_BY_USER_ID)
        )
        statement.setObject(1, UUID.fromString(userUUID))
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            val id = resultSet.getString("uuid")
            val wareId = resultSet.getString("ware_id")
            val quantity = resultSet.getDouble("quantity")

            withdraws.add(
                WithdrawData(
                    uuid = id,
                    userId = userUUID,
                    wareId = wareId,
                    quantity = quantity,
                )
            )
        }

        return@withContext withdraws
    }

    /**
     * Retrieves withdrawals by ware ID from the database.
     *
     * @param wareUUID The UUID of the ware.
     * @return List of withdrawals associated with the ware.
     */
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
            val quantity = resultSet.getDouble("quantity")

            withdraws.add(
                WithdrawData(
                    uuid = id,
                    userId = userId,
                    wareId = wareUUID,
                    quantity = quantity
                )
            )
        }

        return@withContext withdraws
    }
}
