package com.stephen.points.transaction.domain

import com.stephen.points.transaction.dto.TransactionDto

interface TransactionService {
    fun getPointBalances(userId: String): Map<String, Int?>
    fun saveTransaction(userId: String, transactionDto: TransactionDto)
    fun deductPoints(userId: String, points: Int): Map<String, Int?>
}