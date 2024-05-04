package io.lb.warehouse.deposit.data.service

import io.lb.warehouse.core.util.loadQueryFromFile
import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.data.model.DepositData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.VisibleForTesting
import java.sql.Connection
import java.sql.Statement
import java.util.UUID

/**
 * Service class for interacting with the deposit table in the PostgreSQL database.
 *
 * @property connection Connection to the database.
 */
class DepositDatabaseService(private val connection: Connection) {
    /**
     * @suppress
     */
    @VisibleForTesting
    companion object {
        const val CREATE_AFTER_DEPOSIT_TRIGGER = "deposit/create_after_deposit_trigger.sql"
        const val CREATE_UPDATE_QUANTITY_FUNCTION =
            "deposit/create_update_available_quantity_function_on_deposit.sql"
        const val CREATE_TABLE_DEPOSIT = "deposit/create_table_deposit.sql"
        const val INSERT_DEPOSIT = "deposit/insert_deposit.sql"
        const val SELECT_DEPOSIT_BY_ID = "deposit/select_deposit_by_id.sql"
        const val SELECT_DEPOSITS_BY_USER_ID = "deposit/select_deposit_by_user_id.sql"
        const val SELECT_DEPOSITS_BY_WARE_ID = "deposit/select_deposit_by_ware_id.sql"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(loadQueryFromFile(CREATE_TABLE_DEPOSIT))
        statement.executeUpdate(loadQueryFromFile(CREATE_UPDATE_QUANTITY_FUNCTION))
        statement.executeUpdate(loadQueryFromFile(CREATE_AFTER_DEPOSIT_TRIGGER))
    }

    /**
     * Inserts a deposit into the database.
     *
     * @param deposit The deposit data to insert.
     * @return The UUID of the inserted deposit.
     */
    suspend fun insertDeposit(deposit: DepositCreateRequest): String = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            loadQueryFromFile(INSERT_DEPOSIT),
            Statement.RETURN_GENERATED_KEYS
        )
        val uuid = UUID.randomUUID()

        with(statement) {
            setObject(1, uuid)
            setObject(2, UUID.fromString(deposit.userId))
            setObject(3, UUID.fromString(deposit.wareId))
            setDouble(4, deposit.quantity)

            executeUpdate()
        }

        uuid.toString()
    }

    /**
     * Retrieves a deposit by its ID from the database.
     *
     * @param id The ID of the deposit to retrieve.
     * @return The deposit data, or null if not found.
     */
    suspend fun getDepositById(id: String): DepositData? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            loadQueryFromFile(SELECT_DEPOSIT_BY_ID)
        )
        statement.setObject(1, UUID.fromString(id))
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val userId = resultSet.getString("user_id")
            val wareId = resultSet.getString("ware_id")
            val quantity = resultSet.getDouble("quantity")

            return@withContext DepositData(
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
     * Retrieves deposits by user ID from the database.
     *
     * @param userUUID The UUID of the user.
     * @return List of deposits associated with the user.
     */
    suspend fun getDepositsByUserId(userUUID: String): List<DepositData> = withContext(Dispatchers.IO) {
        val deposits = mutableListOf<DepositData>()
        val statement = connection.prepareStatement(
            loadQueryFromFile(SELECT_DEPOSITS_BY_USER_ID)
        )
        statement.setObject(1, UUID.fromString(userUUID))
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            val id = resultSet.getString("uuid")
            val wareId = resultSet.getString("ware_id")
            val quantity = resultSet.getDouble("quantity")

            deposits.add(
                DepositData(
                    uuid = id,
                    userId = userUUID,
                    wareId = wareId,
                    quantity = quantity,
                )
            )
        }

        return@withContext deposits
    }

    /**
     * Retrieves deposits by ware ID from the database.
     *
     * @param wareUUID The UUID of the ware.
     * @return List of deposits associated with the ware.
     */
    suspend fun getDepositsByWareId(wareUUID: String): List<DepositData> = withContext(Dispatchers.IO) {
        val deposits = mutableListOf<DepositData>()
        val statement = connection.prepareStatement(
            loadQueryFromFile(SELECT_DEPOSITS_BY_WARE_ID)
        )
        statement.setObject(1, UUID.fromString(wareUUID))
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            val id = resultSet.getString("uuid")
            val userId = resultSet.getString("user_id")
            val quantity = resultSet.getDouble("quantity")

            deposits.add(
                DepositData(
                    uuid = id,
                    userId = userId,
                    wareId = wareUUID,
                    quantity = quantity
                )
            )
        }

        return@withContext deposits
    }
}
