package io.lb.warehouse.withdraw.data.service

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import io.lb.warehouse.core.util.loadQueryFromFile
import io.lb.warehouse.util.BaseServiceTest
import io.lb.warehouse.withdraw.data.model.WithdrawCreateRequest
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService.Companion.CREATE_TABLE_WITHDRAW
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService.Companion.INSERT_WITHDRAW
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService.Companion.SELECT_WITHDRAWS_BY_USER_ID
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService.Companion.SELECT_WITHDRAWS_BY_WARE_ID
import io.lb.warehouse.withdraw.data.service.WithdrawDatabaseService.Companion.SELECT_WITHDRAW_BY_ID
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.Statement.RETURN_GENERATED_KEYS
import java.util.UUID

class WithdrawDatabaseServiceTest : BaseServiceTest(CREATE_TABLE_WITHDRAW) {
    private lateinit var service: WithdrawDatabaseService

    @BeforeEach
    override fun setUp() {
        super.setUp()
        service = WithdrawDatabaseService(connection)
    }

    @Test
    fun `Instantiating service, should call create table`() {
        verify {
            connection.createStatement()
            statement.executeUpdate(loadQueryFromFile(CREATE_TABLE_WITHDRAW))
        }
    }

    @Test
    fun `Getting unexistent withdraw by ID, should return null`() = runTest {
        val withdrawId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_WITHDRAW_BY_ID))
        } returns preparedStatement
        every { queryResult.next() } returns false

        val result = service.getWithdrawById(withdrawId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_WITHDRAW_BY_ID))
            preparedStatement.setObject(1, UUID.fromString(withdrawId))
            preparedStatement.executeQuery()
        }

        assertThat(result).isNull()
    }

    @Test
    fun `Getting withdraw by ID, should return the correct withdraw`() = runTest {
        val withdrawId = "75ba8951-d1cd-46cb-bde7-39caa35a8929"
        val userId = "8bfcdc8a-5019-410b-afa8-e431ed9be4bc"
        val wareId = "390fddcd-d238-442a-9c44-8c1a3c29cd4e"

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_WITHDRAW_BY_ID))
        } returns preparedStatement
        every { queryResult.next() } returns true

        every { queryResult.getString("user_id") } returns userId
        every { queryResult.getString("ware_id") } returns wareId
        every { queryResult.getDouble("quantity") } returns 500.0
        every { queryResult.getString("timestamp") } returns "2024-05-04 16:37:33.870626-03"

        val result = service.getWithdrawById(withdrawId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_WITHDRAW_BY_ID))
            preparedStatement.setObject(1, UUID.fromString(withdrawId))
            preparedStatement.executeQuery()
        }

        assertThat(result).isNotNull()
        assertThat(result?.uuid).isEqualTo(withdrawId)
        assertThat(result?.userId).isEqualTo(userId)
        assertThat(result?.wareId).isEqualTo(wareId)
        assertThat(result?.quantity).isEqualTo(500.0)
        assertThat(result?.timestamp).isEqualTo("2024-05-04 16:37:33.870626-03")
    }

    @Test
    fun `Getting withdraws by unexistent user ID, should return empty list`() = runTest {
        val userId = "8bfcdc8a-5019-410b-afa8-e431ed9be4bc"

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_WITHDRAWS_BY_USER_ID))
        } returns preparedStatement
        every { queryResult.next() } returns false

        val result = service.getWithdrawsByUserId(userId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_WITHDRAWS_BY_USER_ID))
            preparedStatement.setObject(1, UUID.fromString(userId))
            preparedStatement.executeQuery()
        }

        assertThat(result).isEmpty()
    }

    @Test
    fun `Getting withdraws by unexistent ware ID, should return empty list`() = runTest {
        val wareId = "8bfcdc8a-5019-410b-afa8-e431ed9be4bc"

        every {
            connection.prepareStatement(loadQueryFromFile(SELECT_WITHDRAWS_BY_WARE_ID))
        } returns preparedStatement
        every { queryResult.next() } returns false

        val result = service.getWithdrawsByWareId(wareId)

        verify {
            connection.prepareStatement(loadQueryFromFile(SELECT_WITHDRAWS_BY_WARE_ID))
            preparedStatement.setObject(1, UUID.fromString(wareId))
            preparedStatement.executeQuery()
        }

        assertThat(result).isEmpty()
    }

    @Test
    fun `Creating withdraw, should run succesfully`() = runTest {
        val withdraw = WithdrawCreateRequest(
            userId = "8bfcdc8a-5019-410b-afa8-e431ed9be4bc",
            wareId = "75ba8951-d1cd-46cb-bde7-39caa35a8929",
            quantity = 500.0,
        )

        every {
            connection.prepareStatement(loadQueryFromFile(INSERT_WITHDRAW), RETURN_GENERATED_KEYS)
        } returns preparedStatement

        service.insertWithdraw(withdraw)

        verify {
            connection.prepareStatement(loadQueryFromFile(INSERT_WITHDRAW), RETURN_GENERATED_KEYS)
            preparedStatement.setObject(2, UUID.fromString(withdraw.userId))
            preparedStatement.setObject(3, UUID.fromString(withdraw.wareId))
            preparedStatement.setDouble(4, 500.0)
            preparedStatement.executeUpdate()
        }
    }
}
