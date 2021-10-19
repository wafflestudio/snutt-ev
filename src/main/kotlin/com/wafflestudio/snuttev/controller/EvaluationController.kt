package com.wafflestudio.snuttev.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.wafflestudio.snuttev.dao.model.Lecture
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
        @RequestAttribute(value = "UserId") userId: String,
        @RequestBody @Valid createEvaluationRequest: CreateEvaluationRequest
    ): ResponseEntity<LectureEvaluationDto?> {
        return evaluationService.createEvaluation(userId, createEvaluationRequest)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/api/v1/lecture/{id}/evaluation/")
    fun getLectureEvaluation(
        @PathVariable(value = "id") lectureId: Long
    ): ResponseEntity<List<LectureEvaluationDto>> {
        return evaluationService.getLectureEvaluationsOfLecture(lectureId).let {
            ResponseEntity.ok(it)
        }
    }
}

data class CreateEvaluationRequest(
    @JsonProperty("semester_lecture_id")
    @field:NotNull
    val semesterLectureId: Long,

    @field:NotBlank
    val content: String
)
