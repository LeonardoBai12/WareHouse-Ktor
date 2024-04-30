package io.lb.warehouse.ware.data.service

import io.lb.warehouse.core.util.loadQueryFromFile
import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.data.model.WareData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import jdk.jfr.internal.SecuritySupport.getResourceAsStream

class WareDatabaseService(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE_WARE = "create_table_ware.sql"
        private const val DELETE_WARE = "delete_ware.sql"
        private const val INSERT_WARE = "insert_ware.sql"
        private const val SELECT_WARE_BY_ID = "select_ware_by_id.sql"
        private const val SELECT_WARES_BY_USER_ID = "select_ware_by_user_id.sql"
        private const val UPDATE_WARE = "update_ware.sql"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(loadQueryFromFile(CREATE_TABLE_WARE))
    }

    suspend fun insertWare(ware: WareCreateRequest): String = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            loadQueryFromFile(INSERT_WARE),
            Statement.RETURN_GENERATED_KEYS
        )

        with(statement) {
            setObject(1, UUID.randomUUID())
            setString(2, ware.name)
            setObject(3, UUID.fromString(ware.userId))
            setString(4, ware.description)
            setDouble(5, ware.weightPerUnit)
            setString(6, ware.weightUnit)
            setDouble(7, ware.availableQuantity)
            setString(8, ware.quantityUnit)
            setString(9, ware.wareLocation)
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
        val statement = connection.prepareStatement(
            loadQueryFromFile(SELECT_WARE_BY_ID)
        )
        statement.setObject(1, UUID.fromString(id))
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val userId = resultSet.getString("user_id")
            val name = resultSet.getString("name")
            val description = resultSet.getString("description")
            val weightPerUnit = resultSet.getDouble("weight_per_unit")
            val weightUnit = resultSet.getString("weight_unit")
            val availableQuantity = resultSet.getDouble("available_quantity")
            val quantityUnit = resultSet.getString("quantity_unit")
            val wareLocation = resultSet.getString("ware_location")

            return@withContext WareData(
                uuid = id,
                userId = userId,
                name = name,
                description = description,
                weightPerUnit = weightPerUnit,
                weightUnit = weightUnit,
                availableQuantity = availableQuantity,
                quantityUnit = quantityUnit,
                wareLocation = wareLocation,
            )
        } else {
            null
        }
    }

    suspend fun getWaresByUserId(userUUID: String): List<WareData> = withContext(Dispatchers.IO) {
        val wares = mutableListOf<WareData>()
        val statement = connection.prepareStatement(
            loadQueryFromFile(SELECT_WARES_BY_USER_ID)
        )
        statement.setObject(1, UUID.fromString(userUUID))
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            val id = resultSet.getString("uuid")
            val userId = resultSet.getString("user_id")
            val name = resultSet.getString("name")
            val description = resultSet.getString("description")
            val weightPerUnit = resultSet.getDouble("weight_per_unit")
            val weightUnit = resultSet.getString("weight_unit")
            val availableQuantity = resultSet.getDouble("available_quantity")
            val quantityUnit = resultSet.getString("quantity_unit")
            val wareLocation = resultSet.getString("ware_location")

            wares.add(
                WareData(
                    uuid = id,
                    userId = userId,
                    name = name,
                    description = description,
                    weightPerUnit = weightPerUnit,
                    weightUnit = weightUnit,
                    availableQuantity = availableQuantity,
                    quantityUnit = quantityUnit,
                    wareLocation = wareLocation,
                )
            )
        }

        return@withContext wares
    }

    suspend fun updateWare(id: String, ware: WareCreateRequest) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(UPDATE_WARE))) {
            setString(1, ware.name)
            setString(2, ware.description)
            setDouble(3, ware.weightPerUnit)
            setString(4, ware.weightUnit)
            setDouble(5, ware.availableQuantity)
            setString(6, ware.quantityUnit)
            setString(7, ware.wareLocation)
            setObject(8, UUID.fromString(id))
            executeUpdate()
        }
    }

    suspend fun deleteWare(id: String) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(DELETE_WARE))) {
            setObject(1, UUID.fromString(id))
            executeUpdate()
        }
    }
}
