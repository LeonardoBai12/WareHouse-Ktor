package io.lb.deposithouse.deposit.data.service

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import io.lb.warehouse.core.util.loadQueryFromFile
import io.lb.warehouse.deposit.data.model.DepositCreateRequest
import io.lb.warehouse.deposit.data.service.DepositDatabaseService
import io.lb.warehouse.deposit.data.service.DepositDatabaseService.Companion.CREATE_TABLE_DEPOSIT
import io.lb.warehouse.deposit.data.service.DepositDatabaseService.Companion.INSERT_DEPOSIT
import io.lb.warehouse.deposit.data.service.DepositDatabaseService.Companion.SELECT_DEPOSITS_BY_USER_ID
import io.lb.warehouse.deposit.data.service.DepositDatabaseService.Companion.SELECT_DEPOSITS_BY_WARE_ID
import io.lb.warehouse.deposit.data.service.DepositDatabaseService.Companion.SELECT_DEPOSIT_BY_ID
import io.lb.warehouse.deposit.data.service.DepositDatabaseServiceImpl
import io.lb.warehouse.util.BaseServiceTest
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.Statement
import java.util.UUID

class DepositDatabaseServiceImplTest : BaseServiceTest(CREATE_TABLE_DEPOSIT) {
    private lateinit var service: DepositDatabaseService

    @BeforeEach
    override fun setUp() {
        super.setUp()
        service = DepositDatabaseServiceImpl(connection)
    }

    @Test
    fun `Instantiating service, should call create table`() {
        verify {
            connection.createStatement()
            statement.executeUpdate(loadQueryFromFile(CREATE_TABLE_DEPOSIT))
        }
    }

    @Test
    fun `Getting unexistent deposit by ID, should return null`() = runTest {
        val depositId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_DEPOSIT_BY_ID))
        } returns preparedStatement
        every { queryResult.next() } returns false

        val result = service.getDepositById(depositId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_DEPOSIT_BY_ID))
            preparedStatement.setObject(1, UUID.fromString(depositId))
            preparedStatement.executeQuery()
        }

        assertThat(result).isNull()
    }

    @Test
    fun `Getting deposit by ID, should return the correct deposit`() = runTest {
        val depositId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val userId = "8bfcdc8a-5019-410b-afa8-e431ed9be4bc"
        val wareId = "390fddcd-d238-442a-9c44-8c1a3c29cd4e"

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_DEPOSIT_BY_ID))
        } returns preparedStatement
        every { queryResult.next() } returns true

        every { queryResult.getString("user_id") } returns userId
        every { queryResult.getString("ware_id") } returns wareId
        every { queryResult.getDouble("quantity") } returns 500.0
        every { queryResult.getString("timestamp") } returns "2024-05-04 16:37:33.870626-03"

        val result = service.getDepositById(depositId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_DEPOSIT_BY_ID))
            preparedStatement.setObject(1, UUID.fromString(depositId))
            preparedStatement.executeQuery()
        }

        assertThat(result).isNotNull()
        assertThat(result?.uuid).isEqualTo(depositId)
        assertThat(result?.userId).isEqualTo(userId)
        assertThat(result?.wareId).isEqualTo(wareId)
        assertThat(result?.quantity).isEqualTo(500.0)
        assertThat(result?.timestamp).isEqualTo("2024-05-04 16:37:33.870626-03")
    }

    @Test
    fun `Getting deposits by unexistent user ID, should return empty list`() = runTest {
        val userId = "8bfcdc8a-5019-410b-afa8-e431ed9be4bc"

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_DEPOSITS_BY_USER_ID))
        } returns preparedStatement
        every { queryResult.next() } returns false

        val result = service.getDepositsByUserId(userId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_DEPOSITS_BY_USER_ID))
            preparedStatement.setObject(1, UUID.fromString(userId))
            preparedStatement.executeQuery()
        }

        assertThat(result).isEmpty()
    }

    @Test
    fun `Getting deposits by unexistent ware ID, should return empty list`() = runTest {
        val wareId = "8bfcdc8a-5019-410b-afa8-e431ed9be4bc"

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_DEPOSITS_BY_WARE_ID))
        } returns preparedStatement
        every { queryResult.next() } returns false

        val result = service.getDepositsByWareId(wareId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_DEPOSITS_BY_WARE_ID))
            preparedStatement.setObject(1, UUID.fromString(wareId))
            preparedStatement.executeQuery()
        }

        assertThat(result).isEmpty()
    }

    @Test
    fun `Creating deposit, should run succesfully`() = runTest {
        val deposit = DepositCreateRequest(
            userId = "8bfcdc8a-5019-410b-afa8-e431ed9be4bc",
            wareId = "75ba8951-d1cd-46cb-bde7-39caa35a8929",
            quantity = 500.0,
        )

        every {
            connection.prepareStatement(loadQueryFromFile(INSERT_DEPOSIT), Statement.RETURN_GENERATED_KEYS)
        } returns preparedStatement

        service.insertDeposit(deposit)

        verify {
            connection.prepareStatement(loadQueryFromFile(INSERT_DEPOSIT), Statement.RETURN_GENERATED_KEYS)
            preparedStatement.setObject(2, UUID.fromString(deposit.userId))
            preparedStatement.setObject(3, UUID.fromString(deposit.wareId))
            preparedStatement.setDouble(4, 500.0)
            preparedStatement.executeUpdate()
        }
    }
}
