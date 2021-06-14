package com.stephen.points.transaction.dto

data class TransactionDto(
    val payer: String,
    val points: Int,
    // TODO (Stephen) Use proper OffsetDateTime
    val timestamp: String
)
