package com.stephen.points.transaction.data.h2

import com.stephen.points.transaction.domain.Transaction
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Repository
class TransactionRepository(
    @PersistenceContext
    private val entityManager: EntityManager
) {

    @Transactional
    fun saveTransaction(transaction: Transaction) {
        entityManager.persist(transaction)
    }

    fun getTransactionsByUserId(userId: String): List<Transaction> {
        val query = entityManager.createNativeQuery(
            """
                SELECT * FROM TRANSACTIONS
                WHERE USER_ID='$userId'
            """
        )
        val resultList = query.resultList.toList()
        return resultList.map {
            it as Array<out Any?>

            Transaction(
                transactionId = it[0] as String,
                payer = it[1] as String,
                points = it[2] as Int,
                timestamp = it[3] as String,
                userId = it[4] as String
            )
        }
    }

    @Transactional
    fun updateTransactionPoints(transactionId: String, points: Int) {
        val query =
            entityManager.createNativeQuery(
                """
               UPDATE TRANSACTIONS
               SET POINTS=$points
               WHERE TRANSACTION_ID='$transactionId'
            """
            )
        query.executeUpdate()
    }
}