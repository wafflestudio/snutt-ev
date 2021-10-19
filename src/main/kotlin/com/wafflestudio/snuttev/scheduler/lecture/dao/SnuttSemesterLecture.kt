package com.wafflestudio.snuttev.scheduler.lecture.dao

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "lectures")
class SnuttSemesterLecture(
    val id: String,
    val classification: String,                           // 교과 구분
    val department: String,                               // 학부
    val academic_year: String,                            // 학년
    @Field("course_title")
    val courseTitle: String,   // 과목명
    val credit: Int,                                   // 학점
    @Field("class_time")
    val classTime: String,
    @Field("class_time_json")
    val classTimeJson: List<MongoTimePlace>,
    @Field("class_time_mask")
    val classTimeMask: List<Int>,
    val instructor: String,                               // 강사
    val quota: Int,                                    // 정원
    val remark: String,                                   // 비고
    val category: String,
    val year: Int,           // 연도
    val semester: Int,       // 학기
    @Field("class_Int")
    val courseNumber: String?,   // 교과목 번호
    @Field("lecture_Int")
    val lectureNumber: String?,
)