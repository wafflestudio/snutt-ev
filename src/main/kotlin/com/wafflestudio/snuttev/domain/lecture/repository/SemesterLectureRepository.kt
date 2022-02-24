package com.wafflestudio.snuttev.domain.lecture.repository

import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLectureWithLecture
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SemesterLectureRepository : JpaRepository<SemesterLecture, Long> {
    fun findAllByYearAndSemester(year: Int, semester: Int): List<SemesterLecture>

    @Query("""
        select new com.wafflestudio.snuttev.domain.lecture.model.SemesterLectureWithLecture( 
        sl.id, sl.year, sl.semester, sl.credit, sl.extraInfo, sl.academicYear, sl.category, 
        sl.classification, sl.lecture.id, sl.lecture.title, sl.lecture.instructor, sl.lecture.department, sl.lecture.courseNumber) 
        from SemesterLecture sl 
        where sl.lecture.id = :lectureId 
        order by sl.year desc, sl.semester desc
    """
    )
    fun findAllByLectureIdOrderByYearDescSemesterDesc(lectureId: Long): List<SemesterLectureWithLecture>
}
