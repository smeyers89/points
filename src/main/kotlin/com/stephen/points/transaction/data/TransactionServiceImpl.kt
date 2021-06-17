package com.stephen.points.transaction.data

import com.stephen.points.transaction.data.h2.TransactionRepository
import com.stephen.points.transaction.domain.Transaction
import com.stephen.points.transaction.domain.TransactionService
import com.stephen.points.transaction.dto.TransactionDto
import org.springframework.stereotype.Service
import java.util.*

@Service
class TransactionServiceImpl(
    private val transactionRepository: TransactionRepository
) : TransactionService {

    override fun getPointBalances(userId: String): Map<String, Int?> {

        return transactionRepository.getTransactionsByUserId(userId)
            .groupingBy { it.payer }
            .aggregate { _, points: Int?, transaction, first ->
                if (first)
                    transaction.points
                else
                    points?.plus(transaction.points)
            }
    }

    override fun saveTransaction(userId: String, transactionDto: TransactionDto) {

        // On point deduction transactions, ensure we don't dip a payer into a negative point balance
        if (transactionDto.points < 0) {
            validatePositivePoints(
                transactionRepository.getTransactionsByUserIdAndPayer(userId, transactionDto.payer),
                transactionDto.points
            ).let { valid -> if (!valid) return }
        }

        transactionRepository.saveTransaction(
            Transaction(
                transactionId = UUID.randomUUID().toString(),
                userId = userId,
                payer = transactionDto.payer,
                points = transactionDto.points,
                timestamp = transactionDto.timestamp
            )
        )
    }

    override fun deductPoints(userId: String, points: Int): Map<String, Int?> {

        val transactions = transactionRepository.getTransactionsByUserId(userId)
            .sortedBy { it.timestamp }
            .filter { it.points > 0 }

        if (!validateCurrentPoints(transactions, points)) {
            // User does not have enough points to cover this transaction request.
            return emptyMap()
        }

        var currentPoints = points

        var index = 0
        val numberOfTransactions = transactions.size
        val spentTransactions = mutableListOf<Transaction>()

        while (currentPoints > 0 && index < numberOfTransactions) {

            val transaction = transactions[index]

            if (transaction.points >= currentPoints) {
                transactionRepository.updateTransactionPoints(
                    transaction.transactionId,
                    transaction.points - currentPoints
                )

                // Show the "spent" points back to the caller.
                spentTransactions.add(transaction.copy(points = currentPoints.unaryMinus()))

                // We have "spent" all the points needed for this user request.
                break
            } else {
                currentPoints -= transaction.points

                // We have "spent" all the points on this specific transaction.
                transactionRepository.updateTransactionPoints(transaction.transactionId, 0)

                // Show the "spent" points back to the caller.
                spentTransactions.add(transaction.copy(points = transaction.points.unaryMinus()))
            }

            index++
        }

        return spentTransactions
            .groupingBy { it.payer }
            .aggregate { _, spentPoints: Int?, transaction, first ->
                if (first)
                    transaction.points
                else
                    spentPoints?.plus(transaction.points)
            }
    }

    fun validateCurrentPoints(transactions: List<Transaction>, points: Int): Boolean {
        if (transactions.sumOf { it.points } < points) {
            return false
        }

        return true
    }

    fun validatePositivePoints(transactions: List<Transaction>, points: Int): Boolean {
        if ((transactions.sumOf { it.points } + points) < 0) {
            return false
        }

        return true
    }
}