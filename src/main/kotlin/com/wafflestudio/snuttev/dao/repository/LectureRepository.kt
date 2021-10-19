package com.wafflestudio.snuttev.dao.repository

import com.wafflestudio.snuttev.dao.model.Lecture
import org.springframework.data.jpa.repository.JpaRepository

interface LectureRepository : JpaRepository<Lecture, Long> {
    fun existsByCourseNumberAndInstructor(courseNumber: String?, instructor: String): Boolean
    fun findByCourseNumberAndInstructorAndTitleAndDepartment(
        courseNumber: String?,
        instructor: String,
        title: String,
        department: String
    ): Lecture?
}
