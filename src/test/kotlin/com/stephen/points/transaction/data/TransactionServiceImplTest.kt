package com.stephen.points.transaction.data

import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.stephen.points.transaction.data.h2.TransactionRepository
import com.stephen.points.transaction.domain.Transaction
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.web.server.ResponseStatusException

internal class TransactionServiceImplTest {
    private lateinit var serviceUnderTest: TransactionServiceImpl
    private lateinit var mockTransactionRepository: TransactionRepository
    private val userId = "testUser"
    private val testPayerOne = "testPayerOne"
    private val testPayerTwo = "testPayerTwo"

    @BeforeEach
    fun setup() {
        mockTransactionRepository = mock(TransactionRepository::class.java)
        serviceUnderTest = TransactionServiceImpl(mockTransactionRepository)
    }

    @Test
    fun `getPointBalances() return a point map grouped by payer`() {

        // Arrange
        val expectedBalance = mapOf(testPayerOne to 100, testPayerTwo to 200)

        whenever(
            mockTransactionRepository.getTransactionsByUserId(userId)
        )
            .doReturn(
                listOf(
                    Transaction("transaction-1", userId, testPayerOne, 100, "time.now()"),
                    Transaction("transaction-2", userId, testPayerTwo, 200, "time.now()")
                )
            )

        // Act
        val balance = serviceUnderTest.getPointBalances(userId)

        // Assert
        Assertions.assertThat(balance).isEqualTo(expectedBalance)
    }

    @Test
    fun `deductPoints() return a point map grouped by payer`() {

        // Arrange
        val expectedRemainingBalance = mapOf(testPayerOne to -100, testPayerTwo to -900)

        whenever(
            mockTransactionRepository.getTransactionsByUserId(userId)
        )
            .doReturn(
                listOf(
                    Transaction("transaction-1", userId, testPayerOne, 100, "time.now()"),
                    Transaction("transaction-2", userId, testPayerTwo, 2000, "time.now()")
                )
            )

        doNothing().whenever(mockTransactionRepository).updateTransactionPoints(anyString(), anyInt())

        // Act
        val remainingBalance = serviceUnderTest.deductPoints(userId, 1_000)

        // Assert
        Assertions.assertThat(remainingBalance).isEqualTo(expectedRemainingBalance)
    }

    @Test
    fun `deductPoints() handles a user with no transactions`() {

        // Arrange
        var exceptionMessage = ""

        whenever(mockTransactionRepository.getTransactionsByUserId(userId)).doReturn(emptyList())

        // Act
        try {
            serviceUnderTest.deductPoints(userId, 1_000)
        } catch (ex: ResponseStatusException) {
            exceptionMessage = ex.reason ?: ""
        }

        // Assert
        Assertions.assertThat(exceptionMessage).isEqualTo("User does not have enough points to cover transaction")
    }

    @Test
    fun `deductPoints() handles user with insufficient points`() {

        // Arrange
        var exceptionMessage = ""

        whenever(
            mockTransactionRepository.getTransactionsByUserId(userId)
        )
            .doReturn(
                listOf(
                    Transaction("transaction-1", userId, testPayerOne, 100, "time.now()"),
                    Transaction("transaction-2", userId, testPayerTwo, 200, "time.now()")
                )
            )

        doNothing().whenever(mockTransactionRepository).updateTransactionPoints(anyString(), anyInt())

        // Act
        try {
            serviceUnderTest.deductPoints(userId, 1_000)
        } catch (ex: ResponseStatusException) {
            exceptionMessage = ex.reason ?: ""
        }

        // Assert
        Assertions.assertThat(exceptionMessage).isEqualTo("User does not have enough points to cover transaction")
    }

    @Test
    fun `validateCurrentPoints() returns true if user has enough points to cover transaction`() {

        // Arrange
        val transactions = listOf(
            Transaction("transaction-1", userId, testPayerOne, 100, "time.now()"),
            Transaction("transaction-2", userId, testPayerTwo, 200, "time.now()")
        )

        // Act
        val valid = serviceUnderTest.validateCurrentPoints(transactions, 100)

        // Assert
        Assertions.assertThat(valid).isTrue
    }

    @Test
    fun `validateCurrentPoints() returns false if user does not have enough points to cover transaction`() {

        // Arrange
        val transactions = listOf(
            Transaction("transaction-1", userId, testPayerOne, 100, "time.now()"),
            Transaction("transaction-2", userId, testPayerTwo, 200, "time.now()")
        )

        // Act
        val valid = serviceUnderTest.validateCurrentPoints(transactions, 1_000)

        // Assert
        Assertions.assertThat(valid).isFalse
    }

    @Test
    fun `validatePositivePoints() returns true if a balance is sufficient to cover the point deduction`() {

        // Arrange
        val transactions = listOf(
            Transaction("transaction-1", userId, testPayerOne, 100, "time.now()"),
            Transaction("transaction-2", userId, testPayerTwo, 200, "time.now()")
        )

        // Act
        val valid = serviceUnderTest.validatePositivePoints(transactions, -100)

        // Assert
        Assertions.assertThat(valid).isTrue
    }

    @Test
    fun `validatePositivePoints() returns false if a point deduction would result in a negative balance`() {

        // Arrange
        val transactions = listOf(
            Transaction("transaction-1", userId, testPayerOne, 100, "time.now()"),
            Transaction("transaction-2", userId, testPayerTwo, 200, "time.now()")
        )

        // Act
        val valid = serviceUnderTest.validatePositivePoints(transactions, -1_000)

        // Assert
        Assertions.assertThat(valid).isFalse
    }
}