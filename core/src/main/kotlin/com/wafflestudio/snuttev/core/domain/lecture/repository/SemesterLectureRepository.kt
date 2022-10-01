package com.wafflestudio.snuttev.core.domain.lecture.repository

import com.wafflestudio.snuttev.core.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLectureWithLecture
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SemesterLectureRepository : JpaRepository<SemesterLecture, Long> {

    @Query("SELECT DISTINCT sl FROM SemesterLecture sl JOIN FETCH sl.lecture WHERE sl.year = :year AND sl.semester = :semester")
    fun findAllByYearAndSemesterWithLecture(year: Int, semester: Int): List<SemesterLecture>

    @Query("SELECT DISTINCT sl FROM SemesterLecture sl JOIN FETCH sl.lecture")
    fun findAllWithLecture(): List<SemesterLecture>

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLectureWithLecture( 
        sl.id, sl.year, sl.semester, sl.credit, sl.extraInfo, sl.academicYear, sl.category, 
        sl.classification, sl.lecture.id, sl.lecture.title, sl.lecture.instructor, sl.lecture.department, sl.lecture.courseNumber) 
        from SemesterLecture sl 
        where sl.lecture.id = :lectureId 
        order by sl.year desc, sl.semester desc
    """
    )
    fun findAllByLectureIdOrderByYearDescSemesterDesc(lectureId: Long): List<SemesterLectureWithLecture>

    fun findByYearAndSemesterAndLecture(year: Int, semester: Int, lecture: Lecture): SemesterLecture?
}
