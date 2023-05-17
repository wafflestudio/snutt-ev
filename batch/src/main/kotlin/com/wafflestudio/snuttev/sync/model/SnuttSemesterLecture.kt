package com.wafflestudio.snuttev.sync.model

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "lectures")
class SnuttSemesterLecture(
    val id: String,
    val classification: String,
    val department: String,
    @Field("academic_year")
    val academicYear: String,
    @Field("course_title")
    val courseTitle: String,
    val credit: Int,
    val instructor: String,
    val remark: String,
    val category: String,
    val year: Int,
    val semester: Int,
    @Field("course_number")
    val courseNumber: String,
)
