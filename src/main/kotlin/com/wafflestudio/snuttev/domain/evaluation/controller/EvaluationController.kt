package com.wafflestudio.snuttev.domain.evaluation.controller

import com.wafflestudio.snuttev.domain.evaluation.dto.*
import com.wafflestudio.snuttev.domain.evaluation.service.EvaluationService
import com.wafflestudio.snuttev.domain.lecture.dto.GetSemesterLecturesResponse
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class EvaluationController(
    private val evaluationService: EvaluationService
) {

    @PostMapping("/v1/semester-lectures/{id}/evaluations")
    fun createEvaluation(
        @PathVariable(value = "id") semesterLectureId: Long,
        @RequestBody @Valid createEvaluationRequest: CreateEvaluationRequest,
        @RequestAttribute(value = "UserId") userId: String
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
    ): CursorPaginationResponse {
        return evaluationService.getEvaluationsOfLecture(lectureId, cursor)
    }
}
