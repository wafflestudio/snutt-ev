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
    @Field("class_time")
    val classTime: String,
    @Field("class_time_json")
    val classTimeJson: List<SnuttTimePlace>,
    @Field("class_time_mask")
    val classTimeMask: List<Int>,
    val instructor: String,
    val quota: Int,
    val remark: String,
    val category: String,
    val year: Int,
    val semester: Int,
    @Field("course_number")
    val courseNumber: String,
    @Field("lecture_number")
    val lectureNumber: String,
)