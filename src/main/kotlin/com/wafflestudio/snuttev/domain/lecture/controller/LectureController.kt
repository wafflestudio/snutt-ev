package com.wafflestudio.snuttev.domain.lecture.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wafflestudio.snuttev.domain.common.dto.ListResponse
import com.wafflestudio.snuttev.domain.common.dto.PaginationResponse
import com.wafflestudio.snuttev.domain.lecture.dto.*
import com.wafflestudio.snuttev.domain.lecture.service.LectureService
import org.springframework.web.bind.annotation.*

@RestController
class LectureController(
    private val lectureService: LectureService,
    private val objectMapper: ObjectMapper
) {

    @GetMapping("/v1/lectures")
    fun getLectures(@ModelAttribute params: SearchLectureRequest): PaginationResponse<LectureDto> {
        return PaginationResponse(lectureService.search(params))
    }

    @GetMapping("/v1/lectures/{id}/semester-lectures")
    fun getSemesterLectures(
        @PathVariable(value = "id") lectureId: Long,
    ): LectureAndSemesterLecturesResponse {
        return lectureService.getSemesterLectures(lectureId)
    }

    @GetMapping("/v1/lectures/id")
    fun getLectureId(
        @RequestParam("course_number") courseNumber: String,
        @RequestParam("instructor") instructor: String,
    ): LectureIdResponse {
        return lectureService.getLectureIdFromCourseNumber(courseNumber, instructor)
    }

    @GetMapping("/v1/users/me/lectures/latest")
    fun getLecturesTakenByCurrentUser(
        @RequestParam("snutt_lecture_info") snuttLectureInfoString: String? = "",
    ): ListResponse<LectureTakenByUserResponse> {
        val snuttLectureInfos: List<SnuttLectureInfo> =
            objectMapper.readValue(snuttLectureInfoString ?: "")
        return ListResponse(lectureService.getSnuttevLecturesWithSnuttLectureInfos(snuttLectureInfos))
    }
}
