package com.stephen.points.transaction.domain

import javax.persistence.*

@Entity
@Table(name = "transactions")
data class Transaction(
    @Id
    val transactionId: String,
    val userId: String,
    val payer: String,
    val points: Int,
    // TODO (Stephen) Use proper OffsetDateTime
    val timestamp: String
)
