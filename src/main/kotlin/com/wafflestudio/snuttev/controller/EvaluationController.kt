package com.wafflestudio.snuttev.controller

import com.wafflestudio.snuttev.service.EvaluationService
import com.wafflestudio.snuttev.service.LectureEvaluationDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
class EvaluationController(
    private val evaluationService: EvaluationService
) {

    @PostMapping("/v1/semester-lectures/{id}/evaluations")
    fun createEvaluation(
        @PathVariable(value = "id") semesterLectureId: Long,
        @RequestBody @Valid createEvaluationRequest: CreateEvaluationRequest,
        @RequestAttribute(value = "UserId") userId: String
    ): ResponseEntity<LectureEvaluationDto?> {
        return evaluationService.createEvaluation(userId, semesterLectureId, createEvaluationRequest)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/v1/semester-lectures/{id}/evaluations")
    fun getLectureEvaluation(
        @PathVariable(value = "id") lectureId: Long
    ): ResponseEntity<List<LectureEvaluationDto>> {
        return evaluationService.getLectureEvaluationsOfLecture(lectureId).let {
            ResponseEntity.ok(it)
        }
    }
}

data class CreateEvaluationRequest(
    @field:NotBlank
    val content: String
)
