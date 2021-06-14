package com.stephen.points.transaction.api

import com.stephen.points.transaction.domain.TransactionService
import com.stephen.points.transaction.dto.PointsDto
import com.stephen.points.transaction.dto.TransactionDto
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/transaction/{userId}")
class TransactionController(
    private val service: TransactionService
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPointBalances(
        @PathVariable userId: String,
    ): ResponseEntity<Map<String, Int?>> {

        val pointBalances = service.getPointBalances(userId)

        return ResponseEntity.ok().body(pointBalances)
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addPointBalance(
        @PathVariable userId: String,
        @RequestBody transactionDto: TransactionDto
    ): ResponseEntity<Void> {

        service.saveTransaction(userId, transactionDto)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PatchMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun deductPoints(
        @PathVariable userId: String,
        @RequestBody pointsDto: PointsDto
    ): ResponseEntity<Void> {

        service.deductPoints(userId, pointsDto.points)

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}