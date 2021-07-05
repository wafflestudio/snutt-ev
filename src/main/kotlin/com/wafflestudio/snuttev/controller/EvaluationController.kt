package com.wafflestudio.snuttev.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.wafflestudio.snuttev.service.EvaluationService
import com.wafflestudio.snuttev.service.LectureEvaluationDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@RestController
class EvaluationController(
    private val evaluationService: EvaluationService
) {

    @PostMapping("/api/v1/evaluation/")
    fun createEvaluation(
        @RequestAttribute(value = "UserId") userId: Long,
        @RequestBody @Valid createEvaluationRequest: CreateEvaluationRequest
    ) : ResponseEntity<LectureEvaluationDto?> {
        return evaluationService.createEvaluation(userId, createEvaluationRequest)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }
}

data class CreateEvaluationRequest(
    @JsonProperty("semester_lecture_id")
    @field:NotNull
    val semesterLectureId: Long,

    @field:NotBlank
    val content: String
)
