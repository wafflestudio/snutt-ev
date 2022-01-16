package com.wafflestudio.snuttev.domain.evaluation.controller

import com.wafflestudio.snuttev.domain.evaluation.dto.*
import com.wafflestudio.snuttev.domain.evaluation.service.EvaluationService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class EvaluationController(
    private val evaluationService: EvaluationService,
) {

    @PostMapping("/v1/semester-lectures/{id}/evaluations")
    fun createEvaluation(
        @PathVariable(value = "id") semesterLectureId: Long,
        @RequestBody @Valid createEvaluationRequest: CreateEvaluationRequest,
        @RequestAttribute(value = "UserId") userId: String,
    ): LectureEvaluationDto {
        return evaluationService.createEvaluation(userId, semesterLectureId, createEvaluationRequest)
    }

    @GetMapping("/v1/lectures/{id}/evaluation-summary")
    fun getLectureEvaluationSummary(
        @PathVariable(value = "id") lectureId: Long,
    ): LectureEvaluationSummaryResponse {
        return evaluationService.getEvaluationSummaryOfLecture(lectureId)
    }

    @GetMapping("/v1/lectures/{id}/evaluations")
    fun getLectureEvaluations(
        @PathVariable(value = "id") lectureId: Long,
        @RequestParam("cursor") cursor: String?,
        @RequestAttribute(value = "UserId") userId: String,
    ): CursorPaginationResponse {
        return evaluationService.getEvaluationsOfLecture(userId, lectureId, cursor)
    }

    @GetMapping("/v1/tags/main/{id}/evaluations")
    fun getMainTagEvaluations(
        @PathVariable(value = "id") tagId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ): CursorPaginationResponse {
        return evaluationService.getMainTagEvaluations(userId, tagId)
    }

    @DeleteMapping("/v1/evaluations/{id}")
    fun deleteLectureEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ) {
        return evaluationService.deleteLectureEvaluation(userId, evaluationId)
    }

}
