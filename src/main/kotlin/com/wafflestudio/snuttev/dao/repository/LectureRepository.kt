package com.wafflestudio.snuttev.dao.repository

import com.wafflestudio.snuttev.dao.model.Lecture
import com.wafflestudio.snuttev.dao.model.LectureEvaluationSummaryDao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LectureRepository : JpaRepository<Lecture, Long> {
    fun findByCourseNumberAndInstructor(
        courseNumber: String?,
        instructor: String,
    ): Lecture?

    @Query(
            "select new com.wafflestudio.snuttev.dao.model.LectureEvaluationSummaryDao(" +
                    "sl.lecture.title, sl.lecture.instructor, sl.lecture.department, sl.lecture.courseNumber, " +
                    "sl.lecture.credit, sl.lecture.academicYear, sl.lecture.category, sl.lecture.classification, " +
                    "avg(le.rating) as avgRating, avg(le.gradeSatisfaction) as avgGradeSatisfaction, " +
                    "avg(le.teachingSkill) as avgTeachingSkill, avg(le.gains) as avgGains, avg(le.lifeBalance) as avgLifeBalance) " +
                    "from LectureEvaluation le right join le.semesterLecture sl where sl.lecture.id = :id"
    )
    fun findLectureWithAvgEvById(id: Long): LectureEvaluationSummaryDao
}
