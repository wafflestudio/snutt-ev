package com.wafflestudio.snuttev.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wafflestudio.snuttev.core.common.dto.common.ListResponse
import com.wafflestudio.snuttev.core.common.dto.common.PaginationResponse
import com.wafflestudio.snuttev.core.common.error.ErrorResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.EvLectureSummaryForSnutt
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureAndSemesterLecturesResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureDto
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureIdResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureTakenByUserResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.SearchLectureRequest
import com.wafflestudio.snuttev.core.domain.lecture.dto.SnuttLectureInfo
import com.wafflestudio.snuttev.core.domain.lecture.service.LectureService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LectureController(
    private val lectureService: LectureService,
    private val objectMapper: ObjectMapper,
) {

    @GetMapping("/v1/lectures")
    fun getLectures(@ModelAttribute params: SearchLectureRequest): PaginationResponse<LectureDto> {
        return PaginationResponse(lectureService.search(params))
    }

    @GetMapping("/v1/lectures/{id}/semester-lectures")
    fun getSemesterLectures(
        @PathVariable(value = "id") lectureId: Long,
        @RequestAttribute(value = "UserId") userId: String,
    ): LectureAndSemesterLecturesResponse {
        return lectureService.getSemesterLectures(lectureId, userId)
    }

    @Operation(
        parameters = [
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "course_number",
                description = "course number of the lecture",
                required = true,
            ),
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "instructor",
                description = "instructor of the lecture",
                required = true,
            ),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = LectureIdResponse::class))],
            ),
            ApiResponse(
                responseCode = "409",
                description = "24001 LECTURE_NOT_FOUND",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/v1/lectures/id", params = ["course_number", "instructor"])
    fun getLectureId(
        @RequestParam("course_number") courseNumber: String,
        @RequestParam instructor: String,
    ): LectureIdResponse {
        return lectureService.getLectureIdFromCourseNumber(courseNumber, instructor)
    }

    @Operation(
        parameters = [
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "semesterLectureSnuttId",
                description = "snuttId (mongoId) of the lecture",
                required = true,
            ),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = LectureIdResponse::class))],
            ),
            ApiResponse(
                responseCode = "409",
                description = "24001 LECTURE_NOT_FOUND",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/v1/lectures/id", params = ["semesterLectureSnuttId"])
    fun getLectureId(
        @RequestParam("semesterLectureSnuttId") semesterLectureSnuttId: String,
    ): LectureIdResponse {
        return lectureService.getLectureIdFromSnuttId(semesterLectureSnuttId)
    }

    @Operation(
        parameters = [
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "semesterLectureSnuttIds",
                description = "snuttId (mongoId) of the lecture",
                required = true,
            ),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = LectureIdListResponse::class))],
            ),
            ApiResponse(
                responseCode = "409",
                description = "24001 LECTURE_NOT_FOUND",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/v1/lectures/ids", params = ["semesterLectureSnuttIds"])
    fun getLectureId(
        @RequestParam("semesterLectureSnuttIds") semesterLectureSnuttIds: List<String>,
    ): ListResponse<LectureIdResponse> {
        val lectureIdResponses = lectureService.getLectureIdsFromSnuttIds(semesterLectureSnuttIds)
        return ListResponse(lectureIdResponses)
    }

    @Operation(
        parameters = [
            Parameter(
                `in` = ParameterIn.QUERY,
                name = "snuttId",
                description = "snuttId (mongoId) of the lecture",
                required = true,
            ),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(schema = Schema(implementation = LectureIdListResponse::class))],
            ),
            ApiResponse(
                responseCode = "409",
                description = "24001 LECTURE_NOT_FOUND",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/v1/lectures/snutt-summary")
    fun getEvLectureSummaryForSnutt(
        @RequestParam semesterLectureSnuttIds: List<String>,
    ): ListResponse<EvLectureSummaryForSnutt> {
        val evLectureSummary = lectureService.getEvLectureSummaryForSnutt(semesterLectureSnuttIds)
        return ListResponse(evLectureSummary)
    }

    @GetMapping("/v1/users/me/lectures/latest")
    fun getLecturesTakenByCurrentUser(
        @Parameter(hidden = true)
        @RequestParam("snutt_lecture_info")
        snuttLectureInfoString: String? = "",
        @RequestParam
        filter: String?,
        @RequestAttribute(value = "UserId")
        userId: String,
    ): ListResponse<LectureTakenByUserResponse> {
        val snuttLectureInfos: List<SnuttLectureInfo> =
            objectMapper.readValue(snuttLectureInfoString ?: "")
        val excludeMyEvaluations = filter == "no-my-evaluations"

        return ListResponse(
            lectureService.getSnuttevLecturesWithSnuttLectureInfos(
                userId,
                snuttLectureInfos,
                excludeMyEvaluations,
            ),
        )
    }
}

private class LectureIdListResponse : ListResponse<LectureIdResponse>(listOf())
