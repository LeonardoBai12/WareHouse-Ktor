package io.lb.warehouse.ware.data.service

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import io.lb.warehouse.core.util.loadQueryFromFile
import io.lb.warehouse.util.BaseServiceTest
import io.lb.warehouse.ware.data.model.WareCreateRequest
import io.lb.warehouse.ware.data.service.WareDatabaseService.Companion.CREATE_TABLE_WARE
import io.lb.warehouse.ware.data.service.WareDatabaseService.Companion.DELETE_WARE
import io.lb.warehouse.ware.data.service.WareDatabaseService.Companion.INSERT_WARE
import io.lb.warehouse.ware.data.service.WareDatabaseService.Companion.SELECT_WARES_BY_USER_ID
import io.lb.warehouse.ware.data.service.WareDatabaseService.Companion.SELECT_WARE_BY_ID
import io.lb.warehouse.ware.data.service.WareDatabaseService.Companion.UPDATE_WARE
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.Statement
import java.util.UUID

class WareDatabaseServiceTest : BaseServiceTest(CREATE_TABLE_WARE) {
    private lateinit var service: WareDatabaseService

    @BeforeEach
    override fun setUp() {
        super.setUp()
        service = WareDatabaseService(connection)
    }

    @Test
    fun `Instantiating service, should call create table`() {
        verify {
            connection.createStatement()
            statement.executeUpdate(loadQueryFromFile(CREATE_TABLE_WARE))
        }
    }

    @Test
    fun `Getting unexistent ware by ID, should return null`() = runTest {
        val wareId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_WARE_BY_ID))
        } returns preparedStatement
        every { queryResult.next() } returns false

        val result = service.getWareById(wareId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_WARE_BY_ID))
            preparedStatement.setObject(1, UUID.fromString(wareId))
            preparedStatement.executeQuery()
        }

        assertThat(result).isNull()
    }

    @Test
    fun `Getting ware by ID, should return the correct ware`() = runTest {
        val wareId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val userId = "8bfcdc8a-5019-410b-afa8-e431ed9be4bc"

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_WARE_BY_ID))
        } returns preparedStatement
        every { queryResult.next() } returns true

        every { queryResult.getString("user_id") } returns userId
        every { queryResult.getString("name") } returns "name_value"
        every { queryResult.getString("description") } returns "description_value"
        every { queryResult.getString("brand") } returns "brand_value"
        every { queryResult.getDouble("weight_per_unit") } returns 5.0
        every { queryResult.getString("weight_unit") } returns "weight_unit_value"
        every { queryResult.getDouble("available_quantity") } returns 500.0
        every { queryResult.getString("quantity_unit") } returns "quantity_unit_value"
        every { queryResult.getString("ware_location") } returns "ware_location_value"
        every { queryResult.getString("timestamp") } returns "2024-05-04 16:37:33.870626-03"

        val result = service.getWareById(wareId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_WARE_BY_ID))
            preparedStatement.setObject(1, UUID.fromString(wareId))
            preparedStatement.executeQuery()
        }

        assertThat(result).isNotNull()
        assertThat(result?.uuid).isEqualTo(wareId)
        assertThat(result?.userId).isEqualTo(userId)
        assertThat(result?.name).isEqualTo("name_value")
        assertThat(result?.brand).isEqualTo("brand_value")
        assertThat(result?.description).isEqualTo("description_value")
        assertThat(result?.weightPerUnit).isEqualTo(5.0)
        assertThat(result?.weightUnit).isEqualTo("weight_unit_value")
        assertThat(result?.availableQuantity).isEqualTo(500.0)
        assertThat(result?.quantityUnit).isEqualTo("quantity_unit_value")
        assertThat(result?.wareLocation).isEqualTo("ware_location_value")
        assertThat(result?.timestamp).isEqualTo("2024-05-04 16:37:33.870626-03")
    }

    @Test
    fun `Getting wares by unexistent user ID, should return empty list`() = runTest {
        val userId = "8bfcdc8a-5019-410b-afa8-e431ed9be4bc"

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_WARES_BY_USER_ID))
        } returns preparedStatement
        every { queryResult.next() } returns false

        val result = service.getWaresByUserId(userId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_WARES_BY_USER_ID))
            preparedStatement.setObject(1, UUID.fromString(userId))
            preparedStatement.executeQuery()
        }

        assertThat(result).isEmpty()
    }

    @Test
    fun `Creating ware, should run succesfully`() = runTest {
        val ware = WareCreateRequest(
            userId = "8bfcdc8a-5019-410b-afa8-e431ed9be4bc",
            name = "nameValue",
            brand = "brandValue",
            description = "descriptionValue",
            weightPerUnit = 5.0,
            weightUnit = "weightUnitValue",
            availableQuantity = 500.0,
            quantityUnit = "quantityUnitValue",
            wareLocation = "wareLocationValue",
        )

        every {
            connection.prepareStatement(loadQueryFromFile(INSERT_WARE), Statement.RETURN_GENERATED_KEYS)
        } returns preparedStatement

        service.insertWare(ware)

        verify {
            connection.prepareStatement(loadQueryFromFile(INSERT_WARE), Statement.RETURN_GENERATED_KEYS)
            preparedStatement.setObject(2, UUID.fromString(ware.userId))
            preparedStatement.setString(3, "nameValue")
            preparedStatement.setString(4, "brandValue")
            preparedStatement.setString(5, "descriptionValue")
            preparedStatement.setDouble(6, 5.0)
            preparedStatement.setString(7, "weightUnitValue")
            preparedStatement.setDouble(8, 500.0)
            preparedStatement.setString(9, "quantityUnitValue")
            preparedStatement.setString(10, "wareLocationValue")
            preparedStatement.executeUpdate()
        }
    }

    @Test
    fun `Updating ware, should run succesfully`() = runTest {
        val uuid = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val ware = WareCreateRequest(
            userId = "8bfcdc8a-5019-410b-afa8-e431ed9be4bc",
            name = "nameValue",
            brand = "brandValue",
            description = "descriptionValue",
            weightPerUnit = 5.0,
            weightUnit = "weightUnitValue",
            quantityUnit = "quantityUnitValue",
            wareLocation = "wareLocationValue",
        )

        every {
            connection.prepareStatement(loadQueryFromFile(UPDATE_WARE))
        } returns preparedStatement

        service.updateWare(uuid, ware)

        verify {
            connection.prepareStatement(loadQueryFromFile(UPDATE_WARE))
            preparedStatement.setString(1, "nameValue")
            preparedStatement.setString(2, "brandValue")
            preparedStatement.setString(3, "descriptionValue")
            preparedStatement.setDouble(4, 5.0)
            preparedStatement.setString(5, "weightUnitValue")
            preparedStatement.setString(6, "quantityUnitValue")
            preparedStatement.setString(7, "wareLocationValue")
            preparedStatement.setObject(8, UUID.fromString(uuid))
            preparedStatement.executeUpdate()
        }
    }

    @Test
    fun `Deleting ware by ID, should run succesfully`() = runTest {
        val wareId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        every {
            connection.prepareStatement(loadQueryFromFile(DELETE_WARE))
        } returns preparedStatement

        service.deleteWare(wareId)

        verify {
            connection.prepareStatement(loadQueryFromFile(DELETE_WARE))
            preparedStatement.setObject(1, UUID.fromString(wareId))
            preparedStatement.executeUpdate()
        }
    }
}
