package io.lb.warehouse.ware.data.service

import io.lb.warehouse.core.util.loadQueryFromFile
import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.data.model.WareData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement
import java.util.UUID
import org.jetbrains.annotations.VisibleForTesting

class WareDatabaseService(private val connection: Connection) {
    companion object {
        @VisibleForTesting
        const val CREATE_TABLE_WARE = "ware/create_table_ware.sql"
        @VisibleForTesting
        const val DELETE_WARE = "ware/delete_ware.sql"
        @VisibleForTesting
        const val INSERT_WARE = "ware/insert_ware.sql"
        @VisibleForTesting
        const val SELECT_WARE_BY_ID = "ware/select_ware_by_id.sql"
        @VisibleForTesting
        const val SELECT_WARES_BY_USER_ID = "ware/select_ware_by_user_id.sql"
        @VisibleForTesting
        const val UPDATE_WARE = "ware/update_ware.sql"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(loadQueryFromFile(CREATE_TABLE_WARE))
    }

    suspend fun insertWare(ware: WareCreateRequest): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(
            loadQueryFromFile(INSERT_WARE),
            Statement.RETURN_GENERATED_KEYS
        )

        with(statement) {
            setObject(1, UUID.randomUUID())
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
            val brand = resultSet.getString("brand")
            val weightPerUnit = resultSet.getDouble("weight_per_unit")
            val weightUnit = resultSet.getString("weight_unit")
            val availableQuantity = resultSet.getDouble("available_quantity")
            val quantityUnit = resultSet.getString("quantity_unit")
            val wareLocation = resultSet.getString("ware_location")

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
            val name = resultSet.getString("name")
            val brand = resultSet.getString("brand")
            val description = resultSet.getString("description")
            val weightPerUnit = resultSet.getDouble("weight_per_unit")
            val weightUnit = resultSet.getString("weight_unit")
            val availableQuantity = resultSet.getDouble("available_quantity")
            val quantityUnit = resultSet.getString("quantity_unit")
            val wareLocation = resultSet.getString("ware_location")

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
                )
            )
        }

        return@withContext wares
    }

    suspend fun updateWare(uuid: String, ware: WareCreateRequest) = withContext(Dispatchers.IO) {
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

    suspend fun deleteWare(id: String) = withContext(Dispatchers.IO) {
        with(connection.prepareStatement(loadQueryFromFile(DELETE_WARE))) {
            setObject(1, UUID.fromString(id))
            executeUpdate()
        }
    }
}
