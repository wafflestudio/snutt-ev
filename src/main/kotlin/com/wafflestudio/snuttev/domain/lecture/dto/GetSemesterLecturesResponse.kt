package com.wafflestudio.snuttev.domain.lecture.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.wafflestudio.snuttev.domain.evaluation.dto.SemesterLectureDto

data class GetSemesterLecturesResponse(
    @JsonProperty("semester_lectures")
    val semesterLectures: List<SemesterLectureDto>
)