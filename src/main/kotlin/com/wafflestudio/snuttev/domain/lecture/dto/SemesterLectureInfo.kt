package com.wafflestudio.snuttev.domain.lecture.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture

class SemesterLectureInfo(
    val id: Long,
    @JsonProperty("lecture_number")
    val lectureNumber: String,
    val year: Int,
    val semester: Int,
){
    constructor(semesterLecture: SemesterLecture) : this(
        semesterLecture.id!!,
        semesterLecture.lectureNumber,
        semesterLecture.year,
        semesterLecture.semester
    )
}
