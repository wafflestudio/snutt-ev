package com.wafflestudio.snuttev.core.domain.evaluation.repository

import com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLecture
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LectureEvaluationRepository : JpaRepository<LectureEvaluation, Long>, LectureEvaluationRepositoryCustom {

    fun findByIdAndIsHiddenFalse(id: Long): LectureEvaluation?

    fun existsBySemesterLectureAndUserIdAndIsHiddenFalse(semesterLecture: SemesterLecture, userId: String): Boolean

    @Query("select count(le.id) from LectureEvaluation le inner join le.semesterLecture sl where sl.lecture.id = :lectureId and le.isHidden = false")
    fun countByLectureId(lectureId: Long): Long

    fun countByUserIdAndIsHiddenFalse(userId: String): Long

    @Query("select le.semesterLecture.lecture.id from LectureEvaluation le where le.userId = :userId and le.isHidden = false ")
    fun findLectureIdsByLectureEvaluationUserId(userId: String): List<Long>
}
