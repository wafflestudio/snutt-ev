package com.wafflestudio.snuttev.core.domain.evaluation.repository

import com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LectureEvaluationRepository : JpaRepository<LectureEvaluation, Long>, LectureEvaluationRepositoryCustom {

    fun findByIdAndIsHiddenFalse(id: Long): LectureEvaluation?

    fun existsBySemesterLectureIdAndUserIdAndIsHiddenFalse(semesterLectureId: Long, userId: String): Boolean

    @Query("select count(le.id) from LectureEvaluation le inner join le.semesterLecture sl where sl.lecture.id = :lectureId and le.isHidden = false")
    fun countByLectureId(lectureId: Long): Long

    fun countByUserIdAndIsHiddenFalse(userId: String): Long
}
