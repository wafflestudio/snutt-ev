package com.wafflestudio.snuttev.core.domain.lecture.repository

import com.wafflestudio.snuttev.core.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.core.domain.lecture.model.LectureEvaluationSummaryDao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LectureRepository : JpaRepository<Lecture, Long?>, LectureRepositoryCustom {
    fun findByCourseNumberAndInstructor(courseNumber: String, instructor: String): Lecture?

    @Query("SELECT l FROM Lecture AS l WHERE CONCAT(l.courseNumber,l.instructor) IN :lectureKeys")
    fun findAllByLectureKeys(lectureKeys: Set<String>): List<Lecture>

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.lecture.model.LectureEvaluationSummaryDao(
        sl.lecture.id, sl.lecture.title, sl.lecture.instructor, sl.lecture.department, sl.lecture.courseNumber, 
        sl.lecture.credit, sl.lecture.academicYear, sl.lecture.category, sl.lecture.classification, 
        avg(le.gradeSatisfaction), avg(le.teachingSkill),
        avg(le.gains), avg(le.lifeBalance), avg(le.rating)) 
        from LectureEvaluation le right join le.semesterLecture sl on le.isHidden = false where sl.lecture.id = :id
    """,
    )
    fun findLectureWithAvgEvById(id: Long): LectureEvaluationSummaryDao

    @Query("SELECT l FROM Lecture AS l JOIN SemesterLecture sl WHERE sl.snuttId = :snuttId")
    fun findBySnuttId(snuttId: String): Lecture?
}
