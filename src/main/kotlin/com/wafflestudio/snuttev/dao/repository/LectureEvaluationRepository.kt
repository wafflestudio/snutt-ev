package com.wafflestudio.snuttev.dao.repository

import com.wafflestudio.snuttev.dao.model.LectureEvaluation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LectureEvaluationRepository : JpaRepository<LectureEvaluation, Long> {

    @Query("select le from LectureEvaluation le inner join le.semesterLecture sl where sl.lecture.id = ?1")
    fun findByLectureId(lectureId: Long): List<LectureEvaluation>
}
