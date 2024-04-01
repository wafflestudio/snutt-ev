package com.wafflestudio.snuttev.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wafflestudio.snuttev.core.common.dto.common.ListResponse
import com.wafflestudio.snuttev.core.common.dto.common.PaginationResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureAndSemesterLecturesResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureDto
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureIdResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureRatingResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureTakenByUserResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.SearchLectureRequest
import com.wafflestudio.snuttev.core.domain.lecture.dto.SnuttLectureInfo
import com.wafflestudio.snuttev.core.domain.lecture.service.LectureService
import io.swagger.v3.oas.annotations.Parameter
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

    @GetMapping("/v1/lectures/id")
    fun getLectureId(
        @RequestParam("course_number") courseNumber: String,
        @RequestParam("instructor") instructor: String,
    ): LectureIdResponse {
        return lectureService.getLectureIdFromCourseNumber(courseNumber, instructor)
    }

    @GetMapping("/v1/lecturesBySnutt/{semesterLectureSnuttId}/id")
    fun getLectureId(
        @PathVariable(value = "semesterLectureSnuttId") semesterLectureCoreId: String,
    ): LectureIdResponse {
        val lectureId = lectureService.getLectureIdFromSnuttId(semesterLectureCoreId)
        return LectureIdResponse(lectureId)
    }

    @GetMapping("/v1/lecturesBySnutt/ratings")
    fun getLectureRatings(
        @RequestParam("semesterLectureSnuttIds") semesterLectureCoreIds: List<String>,
    ): ListResponse<LectureRatingResponse> {
        val lectureRatings = lectureService.getLectureRatings(semesterLectureCoreIds)
        return ListResponse(lectureRatings)
    }

    @GetMapping("/v1/users/me/lectures/latest")
    fun getLecturesTakenByCurrentUser(
        @Parameter(hidden = true)
        @RequestParam("snutt_lecture_info")
        snuttLectureInfoString: String? = "",
        @RequestParam("filter")
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
