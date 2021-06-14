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

    override fun deductPoints(userId: String, points: Int) {
        TODO("Not yet implemented")
    }

    override fun getPointBalances(userId: String): Map<String, Int?> {

        return transactionRepository.getTransactionByUserId(userId)
            .groupingBy { it.payer }
            .aggregate { _, points: Int?, transaction, first ->
                if (first)
                    transaction.points
                else
                  points?.plus(transaction.points)
            }
    }

    override fun saveTransaction(userId: String, transactionDto: TransactionDto) {

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
}