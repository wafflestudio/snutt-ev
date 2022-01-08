package com.wafflestudio.snuttev.domain.lecture.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.wafflestudio.snuttev.domain.lecture.model.Lecture

data class LectureInfo (
    val id: Long,
    val title: String,
    val instructor: String,
    val department: String,
    @JsonProperty("course_number")
    val courseNumber: String,
    val credit: Int,
    @JsonProperty("academic_year")
    val academicYear: String,
    val category: String,
    val classification: String,
    @JsonProperty("semester_lectures")
    val semesterLectures: List<SemesterLectureInfo> = listOf()
){
    constructor(lecture: Lecture) : this(
        lecture.id!!,
        lecture.title,
        lecture.instructor,
        lecture.department,
        lecture.courseNumber,
        lecture.credit,
        lecture.academicYear,
        lecture.category,
        lecture.classification,
        lecture.semesterLectures.map { SemesterLectureInfo(it) }
    )
}
