package com.wafflestudio.snuttev.scheduler.lecture.dao

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "lectures")
class SnuttSemesterLecture(
    val _id: String?,
    val classification: String,                           // 교과 구분
    val department: String,                               // 학부
    val academic_year: String,                            // 학년
    val course_title: String,   // 과목명
    val credit: Int,                                   // 학점
    val class_time: String,
    val class_time_json: List<MongoTimePlace>,
    val class_time_mask: List<Int>,
    val instructor: String,                               // 강사
    val quota: Int,                                    // 정원
    val remark: String,                                   // 비고
    val category: String,
    val year: Int,           // 연도
    val semester: Int,       // 학기
    val course_Int: String?,   // 교과목 번호
    val lecture_Int: String?,
)