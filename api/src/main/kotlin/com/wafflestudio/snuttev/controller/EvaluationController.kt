package com.wafflestudio.snuttev.controller

import com.wafflestudio.snuttev.domain.evaluation.service.EvaluationService
import com.wafflestudio.snuttev.common.dto.common.CursorPaginationResponse
import com.wafflestudio.snuttev.common.error.ErrorResponse
import com.wafflestudio.snuttev.domain.evaluation.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class EvaluationController(
    private val evaluationService: EvaluationService,
) {

    @Operation(responses = [
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "409", description = "29001 EVALUATION_ALREADY_EXISTS", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
    ])
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

    @Operation(description = "해당 강의의 강의평 전체 수를 total_count, 자신의 강의평을 제외한 강의평들을 content로 제공")
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

    @Operation(responses = [
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "409", description = "29003 EVALUATION_REPORT_ALREADY_EXISTS", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
    ])
    @PostMapping("/v1/evaluations/{id}/report")
    fun reportLectureEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestBody @Valid createEvaluationReportRequest: CreateEvaluationReportRequest,
        @RequestAttribute(value = "UserId") userId: String,
    ): EvaluationReportDto {
        return evaluationService.reportLectureEvaluation(userId, evaluationId, createEvaluationReportRequest)
    }
}
