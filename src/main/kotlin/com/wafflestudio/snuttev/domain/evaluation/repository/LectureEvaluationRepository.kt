package com.wafflestudio.snuttev.domain.evaluation.repository

import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LectureEvaluationRepository : JpaRepository<LectureEvaluation, Long> {

    fun existsBySemesterLectureIdAndUserIdAndIsHiddenFalse(semesterLectureId: Long, userId: String): Boolean

    @Query("select count(le.id) from LectureEvaluation le inner join le.semesterLecture sl where sl.lecture.id = :lectureId")
    fun countByLectureId(lectureId: Long): Long

    @Query("""
        select new com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, sl.year, sl.semester) 
        from LectureEvaluation le inner join le.semesterLecture sl where sl.lecture.id = :lectureId 
        order by sl.year desc, sl.semester desc, le.id desc
    """
    )
    fun findByLectureIdOrderByDesc(lectureId: Long, pageable: Pageable): List<LectureEvaluationWithSemester>

    @Query("""
        select 
        le.id as id, le.user_id as userId, le.content as content, le.grade_satisfaction as gradeSatisfaction, le.teaching_skill as teachingSkill, le.gains as gains, le.life_balance as lifeBalance, le.rating as rating, 
        le.like_count as likeCount, le.dislike_count as dislikeCount, le.is_hidden as isHidden, le.is_reported as isReported, sl.year as year, sl.semester as semester 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where sl.lecture_id = :lectureId 
        and (sl.year, sl.semester, le.id) < (:cursorYear, :cursorSemester, :cursorId)
        order by sl.year desc, sl.semester desc, le.id desc
    """, nativeQuery = true
    )
    fun findByLectureIdLessThanOrderByDesc(lectureId: Long, cursorYear:Int, cursorSemester: Int, cursorId: Long, pageable: Pageable): List<Map<String, Any>>

    @Query("""
        select 1 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where sl.lecture_id = :lectureId 
        and (sl.year, sl.semester, le.id) < (:cursorYear, :cursorSemester, :cursorId) 
        limit 1
    """, nativeQuery = true
    )
    fun existsByLectureIdLessThan(lectureId: Long, cursorYear: Int, cursorSemester: Int, cursorId: Long): Int?
}
