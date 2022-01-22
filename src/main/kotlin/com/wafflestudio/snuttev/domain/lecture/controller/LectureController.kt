package com.wafflestudio.snuttev.domain.lecture.controller

import com.wafflestudio.snuttev.domain.lecture.dto.GetSemesterLecturesResponse
import com.wafflestudio.snuttev.domain.lecture.dto.SearchLectureRequest
import com.wafflestudio.snuttev.domain.lecture.dto.SearchLectureResponse
import com.wafflestudio.snuttev.domain.lecture.service.LectureService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class LectureController(
    private val lectureService: LectureService,
) {

    @GetMapping("/v1/lectures")
    fun getLectures(@ModelAttribute params: SearchLectureRequest): Page<SearchLectureResponse> {
        return lectureService.search(params)
    }

    @GetMapping("/v1/lectures/{id}/semester-lectures")
    fun getSemesterLectures(
        @PathVariable(value = "id") lectureId: Long,
    ): GetSemesterLecturesResponse {
        return lectureService.getSemesterLectures(lectureId)
    }
}
