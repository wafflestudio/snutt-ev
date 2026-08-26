package com.wafflestudio.snuttev.controller

import com.wafflestudio.snuttev.core.common.dto.common.CursorPaginationResponse
import com.wafflestudio.snuttev.core.common.error.ErrorResponse
import com.wafflestudio.snuttev.core.common.type.EvaluationSort
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
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
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
            ApiResponse(
                responseCode = "409",
                description = "29001 EVALUATION_ALREADY_EXISTS",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @PostMapping("/v1/semester-lectures/{id}/evaluations")
    fun createEvaluation(
        @PathVariable(value = "id") semesterLectureId: Long,
        @RequestBody @Valid
        createEvaluationRequest: CreateEvaluationRequest,
        @RequestAttribute(value = "UserId") userId: String,
    ): LectureEvaluationDto = evaluationService.createEvaluation(userId, semesterLectureId, createEvaluationRequest)

    @Operation(
        description = "강의 평가 요약 조회. 강의 기본 정보와 평균 점수, 강의평 개수(evaluation.evaluationCount)를 반환한다. 숨김 처리된 강의평은 제외된다.",
        responses = [
            ApiResponse(responseCode = "200"),
            ApiResponse(
                responseCode = "404",
                description = "24001 LECTURE_NOT_FOUND",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/v1/lectures/{id}/evaluation-summary")
    fun getLectureEvaluationSummary(
        @PathVariable(value = "id") lectureId: Long,
    ): LectureEvaluationSummaryResponse = evaluationService.getEvaluationSummaryOfLecture(lectureId)

    @Operation(
        description = "강의별 강의평 목록 조회 (커서 페이지네이션). total_count는 필터 후 숨김 제외 전체 개수, content는 본인 제외. sort는 latest/recommended.",
        parameters = [
            Parameter(
                `in` = ParameterIn.PATH,
                name = "id",
                description = "강의 ID",
                required = true,
            ),
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "cursor",
                description = "다음 페이지 커서(Base64). latest: year/semester/id, recommended: likeCount/id. sort 변경 시 null 권장",
                required = false,
            ),
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "sort",
                description = "정렬 방식. latest(기본값) 또는 recommended",
                required = false,
                schema = Schema(allowableValues = ["latest", "recommended"]),
            ),
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "year",
                description = "필터링할 수강 연도. 예: 2024. null이면 전체 연도",
                required = false,
            ),
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "semester",
                description = "필터링할 수강 학기. 1=봄, 2=여름, 3=가을, 4=겨울. year와 함께 사용하는 것을 권장",
                required = false,
            ),
        ],
        responses = [
            ApiResponse(responseCode = "200"),
            ApiResponse(
                responseCode = "400",
                description = "20001 WRONG_CURSOR_FORMAT, 20005 INVALID_EVALUATION_SORT (sort가 latest|recommended가 아닌 경우)",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/v1/lectures/{id}/evaluations")
    fun getLectureEvaluations(
        @PathVariable(value = "id") lectureId: Long,
        @RequestParam cursor: String?,
        @RequestParam(required = false) sort: String?,
        @RequestParam(required = false) year: Int?,
        @RequestParam(required = false) semester: Int?,
        @RequestAttribute(value = "UserId") userId: String,
    ): CursorPaginationResponse<EvaluationWithSemesterResponse> =
        evaluationService.getEvaluationsOfLecture(
            userId,
            lectureId,
            cursor,
            EvaluationSort.fromParameter(sort),
            year,
            semester,
        )

    @GetMapping("/v1/lectures/{id}/evaluations/users/me")
    fun getLectureEvaluationsOfMe(
        @PathVariable(value = "id") lectureId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ): EvaluationsResponse = evaluationService.getMyEvaluationsOfLecture(userId, lectureId)

    @GetMapping("/v1/evaluations/users/me")
    fun getEvaluationsOfMe(
        @RequestParam cursor: String?,
        @RequestAttribute(value = "UserId") userId: String,
    ): CursorPaginationResponse<EvaluationWithLectureResponse> = evaluationService.getMyEvaluations(userId, cursor)

    @GetMapping("/v1/tags/main/{id}/evaluations")
    fun getMainTagEvaluations(
        @PathVariable(value = "id") tagId: Long,
        @RequestParam cursor: String?,
        @RequestAttribute(value = "UserId") userId: String,
    ): CursorPaginationResponse<EvaluationWithLectureResponse> = evaluationService.getMainTagEvaluations(userId, tagId, cursor)

    @GetMapping("/v1/evaluations/{id}")
    fun getLectureEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ): EvaluationWithSemesterResponse = evaluationService.getEvaluation(userId, evaluationId)

    @PatchMapping("/v1/evaluations/{id}")
    fun updateLectureEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestBody @Valid
        updateEvaluationRequest: UpdateEvaluationRequest,
        @RequestAttribute(value = "UserId") userId: String,
    ): EvaluationWithSemesterResponse = evaluationService.updateEvaluation(userId, evaluationId, updateEvaluationRequest)

    @DeleteMapping("/v1/evaluations/{id}")
    fun deleteLectureEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ) = evaluationService.deleteEvaluation(userId, evaluationId)

    @Operation(
        responses = [
            ApiResponse(responseCode = "200"),
            ApiResponse(
                responseCode = "409",
                description = "29003 EVALUATION_REPORT_ALREADY_EXISTS",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @PostMapping("/v1/evaluations/{id}/report")
    fun reportLectureEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestBody @Valid
        createEvaluationReportRequest: CreateEvaluationReportRequest,
        @RequestAttribute(value = "UserId") userId: String,
    ): EvaluationReportDto = evaluationService.reportEvaluation(userId, evaluationId, createEvaluationReportRequest)

    @PostMapping("/v1/evaluations/{id}/likes")
    fun likeEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ) = evaluationService.likeEvaluation(userId, evaluationId)

    @DeleteMapping("/v1/evaluations/{id}/likes")
    fun cancelLikeEvaluation(
        @PathVariable(value = "id") evaluationId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ) = evaluationService.cancelLikeEvaluation(userId, evaluationId)
}
