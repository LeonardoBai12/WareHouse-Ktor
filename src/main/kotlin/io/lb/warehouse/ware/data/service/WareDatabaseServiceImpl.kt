package io.lb.warehouse.ware.data.service

import io.lb.warehouse.core.util.loadQueryFromFile
import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.data.model.WareData
import io.lb.warehouse.ware.data.service.WareDatabaseService.Companion.CREATE_TABLE_WARE
import io.lb.warehouse.ware.data.service.WareDatabaseService.Companion.DELETE_WARE
import io.lb.warehouse.ware.data.service.WareDatabaseService.Companion.INSERT_WARE
import io.lb.warehouse.ware.data.service.WareDatabaseService.Companion.SELECT_WARES_BY_USER_ID
import io.lb.warehouse.ware.data.service.WareDatabaseService.Companion.SELECT_WARE_BY_ID
import io.lb.warehouse.ware.data.service.WareDatabaseService.Companion.UPDATE_WARE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement
import java.util.UUID

/**
 * Service class for interacting with the ware table in the PostgreSQL database.
 *
 * @property connection Connection to the database.
 */
class WareDatabaseServiceImpl(private val connection: Connection) : WareDatabaseService {
    init {
        val statement = connection.createStatement()
        statement.executeUpdate(loadQueryFromFile(CREATE_TABLE_WARE))
    }

    /**
     * Inserts a new ware into the database.
     *
     * @param ware The ware data to insert.
     * @return The UUID of the inserted ware.
     */
    override suspend fun insertWare(ware: WareCreateRequest): String = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            loadQueryFromFile(INSERT_WARE),
            Statement.RETURN_GENERATED_KEYS
        )
        val uuid = UUID.randomUUID()

        with(statement) {
            setObject(1, uuid)
            setObject(2, UUID.fromString(ware.userId))
            setString(3, ware.name)
            setString(4, ware.brand)
            setString(5, ware.description)
            setDouble(6, ware.weightPerUnit)
            setString(7, ware.weightUnit)
            setDouble(8, ware.availableQuantity)
            setString(9, ware.quantityUnit)
            setString(10, ware.wareLocation)
            executeUpdate()
        }

        uuid.toString()
    }

    /**
     * Retrieves a ware by its ID from the database.
     *
     * @param id The ID of the ware to retrieve.
     * @return The ware data, or null if not found.
     */
    override suspend fun getWareById(id: String): WareData? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            loadQueryFromFile(SELECT_WARE_BY_ID)
        )
        statement.setObject(1, UUID.fromString(id))
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val userId = resultSet.getString("user_id")
            val name = resultSet.getString("name")
            val description = resultSet.getString("description")
            val brand = resultSet.getString("brand")
            val weightPerUnit = resultSet.getDouble("weight_per_unit")
            val weightUnit = resultSet.getString("weight_unit")
            val availableQuantity = resultSet.getDouble("available_quantity")
            val quantityUnit = resultSet.getString("quantity_unit")
            val wareLocation = resultSet.getString("ware_location")
            val timestamp = resultSet.getString("timestamp")

            return@withContext WareData(
                uuid = id,
                userId = userId,
                name = name,
                brand = brand,
                description = description,
                weightPerUnit = weightPerUnit,
                weightUnit = weightUnit,
                availableQuantity = availableQuantity,
                quantityUnit = quantityUnit,
                wareLocation = wareLocation,
                timestamp = timestamp
            )
        } else {
            null
        }
    }

    /**
     * Retrieves wares by user ID from the database.
     *
     * @param userUUID The UUID of the user.
     * @return List of wares associated with the user.
     */
    override suspend fun getWaresByUserId(userUUID: String): List<WareData> = withContext(Dispatchers.IO) {
        val wares = mutableListOf<WareData>()
        val statement = connection.prepareStatement(
            loadQueryFromFile(SELECT_WARES_BY_USER_ID)
        )
        statement.setObject(1, UUID.fromString(userUUID))
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            val id = resultSet.getString("uuid")
            val name = resultSet.getString("name")
            val brand = resultSet.getString("brand")
            val description = resultSet.getString("description")
            val weightPerUnit = resultSet.getDouble("weight_per_unit")
            val weightUnit = resultSet.getString("weight_unit")
            val availableQuantity = resultSet.getDouble("available_quantity")
            val quantityUnit = resultSet.getString("quantity_unit")
            val wareLocation = resultSet.getString("ware_location")
            val timestamp = resultSet.getString("timestamp")

            wares.add(
                WareData(
                    uuid = id,
                    userId = userUUID,
                    name = name,
                    brand = brand,
                    description = description,
                    weightPerUnit = weightPerUnit,
                    weightUnit = weightUnit,
                    availableQuantity = availableQuantity,
                    quantityUnit = quantityUnit,
                    wareLocation = wareLocation,
                    timestamp = timestamp
                )
            )
        }

        return@withContext wares
    }

    /**
     * Updates a ware in the database.
     *
     * @param uuid The UUID of the ware to update.
     * @param ware The updated ware data.
     */
    override suspend fun updateWare(uuid: String, ware: WareCreateRequest) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(UPDATE_WARE))) {
            setString(1, ware.name)
            setString(2, ware.brand)
            setString(3, ware.description)
            setDouble(4, ware.weightPerUnit)
            setString(5, ware.weightUnit)
            setString(6, ware.quantityUnit)
            setString(7, ware.wareLocation)
            setObject(8, UUID.fromString(uuid))
            executeUpdate()
        }
    }

    /**
     * Deletes a ware from the database.
     *
     * @param id The ID of the ware to delete.
     */
    override suspend fun deleteWare(id: String) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(DELETE_WARE))) {
            setObject(1, UUID.fromString(id))
            executeUpdate()
        }
    }
}
