package com.wafflestudio.snuttev.controller

import com.wafflestudio.snuttev.core.common.dto.common.CursorPaginationResponse
import com.wafflestudio.snuttev.core.common.error.ErrorResponse
import com.wafflestudio.snuttev.core.domain.evaluation.dto.CreateEvaluationReportRequest
import com.wafflestudio.snuttev.core.domain.evaluation.dto.CreateEvaluationRequest
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationReportDto
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationWithLectureResponse
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationWithSemesterResponse
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationsResponse
import com.wafflestudio.snuttev.core.domain.evaluation.dto.LectureEvaluationDto
import com.wafflestudio.snuttev.core.domain.evaluation.dto.LectureEvaluationSummaryResponse
import com.wafflestudio.snuttev.core.domain.evaluation.dto.UpdateEvaluationRequest
import com.wafflestudio.snuttev.core.domain.evaluation.service.EvaluationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class EvaluationController(
    private val evaluationService: EvaluationService,
) {
    @Operation(
        responses = [
            ApiResponse(responseCode = "200"),
            ApiResponse(responseCode = "409", description = "29001 EVALUATION_ALREADY_EXISTS", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
        ],
    )
    @PostMapping("/v1/semester-lectures/{id}/evaluations")
    fun createEvaluation(
        @PathVariable(value = "id") semesterLectureId: Long,
        @RequestBody @Valid
        createEvaluationRequest: CreateEvaluationRequest,
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
        @RequestParam cursor: String?,
        @RequestAttribute(value = "UserId") userId: String,
    ): CursorPaginationResponse<EvaluationWithSemesterResponse> {
        return evaluationService.getEvaluationsOfLecture(userId, lectureId, cursor)
    }

    @GetMapping("/v1/lectures/{id}/evaluations/users/me")
    fun getLectureEvaluationsOfMe(
        @PathVariable(value = "id") lectureId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ): EvaluationsResponse {
        return evaluationService.getMyEvaluationsOfLecture(userId, lectureId)
    }

    @GetMapping("/v1/evaluations/users/me")
    fun getEvaluationsOfMe(
        @RequestParam cursor: String?,
        @RequestAttribute(value = "UserId") userId: String,
    ): CursorPaginationResponse<EvaluationWithLectureResponse> {
        return evaluationService.getMyEvaluations(userId, cursor)
    }

    @GetMapping("/v1/tags/main/{id}/evaluations")
    fun getMainTagEvaluations(
        @PathVariable(value = "id") tagId: Long,
        @RequestParam cursor: String?,
        @RequestAttribute(value = "UserId") userId: String,
    ): CursorPaginationResponse<EvaluationWithLectureResponse> {
        return evaluationService.getMainTagEvaluations(userId, tagId, cursor)
    }

    @GetMapping("/v1/evaluations/{id}")
    fun getLectureEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ): EvaluationWithSemesterResponse {
        return evaluationService.getEvaluation(userId, evaluationId)
    }

    @PatchMapping("/v1/evaluations/{id}")
    fun updateLectureEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestBody @Valid
        updateEvaluationRequest: UpdateEvaluationRequest,
        @RequestAttribute(value = "UserId") userId: String,
    ): EvaluationWithSemesterResponse {
        return evaluationService.updateEvaluation(userId, evaluationId, updateEvaluationRequest)
    }

    @DeleteMapping("/v1/evaluations/{id}")
    fun deleteLectureEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ) {
        return evaluationService.deleteEvaluation(userId, evaluationId)
    }

    @Operation(
        responses = [
            ApiResponse(responseCode = "200"),
            ApiResponse(responseCode = "409", description = "29003 EVALUATION_REPORT_ALREADY_EXISTS", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
        ],
    )
    @PostMapping("/v1/evaluations/{id}/report")
    fun reportLectureEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestBody @Valid
        createEvaluationReportRequest: CreateEvaluationReportRequest,
        @RequestAttribute(value = "UserId") userId: String,
    ): EvaluationReportDto {
        return evaluationService.reportEvaluation(userId, evaluationId, createEvaluationReportRequest)
    }

    @PostMapping("/v1/evaluations/{id}/likes")
    fun likeEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ) {
        return evaluationService.likeEvaluation(userId, evaluationId)
    }

    @DeleteMapping("/v1/evaluations/{id}/likes")
    fun cancelLikeEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ) {
        return evaluationService.cancelLikeEvaluation(userId, evaluationId)
    }
}
