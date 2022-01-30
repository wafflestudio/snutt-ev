package com.wafflestudio.snuttev.domain.evaluation.controller

import com.wafflestudio.snuttev.domain.common.dto.CursorPaginationResponse
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
    ): CursorPaginationResponse<LectureEvaluationWithSemesterDto> {
        return evaluationService.getEvaluationsOfLecture(userId, lectureId, cursor)
    }

    @GetMapping("/v1/lectures/{id}/evaluations/users/me")
    fun getLectureEvaluationsOfMe(
        @PathVariable(value = "id") lectureId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ): LectureEvaluationsResponse {
        return evaluationService.getMyEvaluationsOfLecture(userId, lectureId)
    }

    @GetMapping("/v1/tags/main/{id}/evaluations")
    fun getMainTagEvaluations(
        @PathVariable(value = "id") tagId: Long,
        @RequestParam("cursor") cursor: String?,
        @RequestAttribute(value = "UserId") userId: String,
    ): CursorPaginationResponse<LectureEvaluationWithLectureDto> {
        return evaluationService.getMainTagEvaluations(userId, tagId, cursor)
    }

    @DeleteMapping("/v1/evaluations/{id}")
    fun deleteLectureEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ) {
        return evaluationService.deleteLectureEvaluation(userId, evaluationId)
    }

    @PostMapping("/v1/evaluations/{id}/report")
    fun reportLectureEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestBody @Valid createEvaluationReportRequest: CreateEvaluationReportRequest,
        @RequestAttribute(value = "UserId") userId: String,
    ): EvaluationReportDto {
        return evaluationService.reportLectureEvaluation(userId, evaluationId, createEvaluationReportRequest)
    }

}
