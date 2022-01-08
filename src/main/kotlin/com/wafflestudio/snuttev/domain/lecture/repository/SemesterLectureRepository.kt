package com.wafflestudio.snuttev.domain.lecture.repository

import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture
import org.springframework.data.jpa.repository.JpaRepository

interface SemesterLectureRepository : JpaRepository<SemesterLecture, Long> {
    fun findAllByYearAndSemester(year: Int, semester: Int): List<SemesterLecture>

}
