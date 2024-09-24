package com.wafflestudio.snuttev.core.domain.lecture.repository

import com.wafflestudio.snuttev.core.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.core.domain.lecture.model.LectureEvaluationSummaryDao
import com.wafflestudio.snuttev.core.domain.lecture.model.LectureRatingDao
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

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.lecture.model.LectureRatingDao(
        sl.lecture.id, avg(le.rating), count(le.id)
        ) from LectureEvaluation le right join le.semesterLecture sl where sl.lecture.id in :ids and le.isHidden = false group by sl.lecture.id 
        """,
    )
    fun findAllRatingsByLectureIds(ids: Iterable<Long>): List<LectureRatingDao>

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.lecture.model.LectureRatingDao(
        sl.lecture.id, avg(le.rating), count(le.id)
        ) from LectureEvaluation le right join le.semesterLecture sl where le.isHidden = false group by sl.lecture.id 
        """,
    )
    fun findAllRatings(): List<LectureRatingDao>
}
