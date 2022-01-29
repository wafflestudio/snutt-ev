package com.wafflestudio.snuttev.domain.evaluation.repository

import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LectureEvaluationRepository : JpaRepository<LectureEvaluation, Long> {

    fun findByIdAndIsHiddenFalse(id: Long): LectureEvaluation?

    fun existsBySemesterLectureIdAndUserIdAndIsHiddenFalse(semesterLectureId: Long, userId: String): Boolean

    @Query("select count(le.id) from LectureEvaluation le inner join le.semesterLecture sl where sl.lecture.id = :lectureId and le.isHidden = false")
    fun countByLectureId(lectureId: Long): Long

    @Query("""
        select new com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, sl.year, sl.semester, sl.lecture.id) 
        from LectureEvaluation le inner join le.semesterLecture sl where sl.lecture.id = :lectureId and le.isHidden = false 
        order by sl.year desc, sl.semester desc, le.id desc
    """
    )
    fun findByLectureIdOrderByDesc(lectureId: Long, pageable: Pageable): List<LectureEvaluationWithSemester>

    @Query("""
        select 
        le.id as id, le.user_id as userId, le.content as content, le.grade_satisfaction as gradeSatisfaction, le.teaching_skill as teachingSkill, le.gains as gains, le.life_balance as lifeBalance, le.rating as rating, 
        le.like_count as likeCount, le.dislike_count as dislikeCount, le.is_hidden as isHidden, le.is_reported as isReported, sl.year as year, sl.semester as semester, sl.lecture_id as lectureId 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where sl.lecture_id = :lectureId and le.is_hidden = false 
        and (sl.year, sl.semester, le.id) < (:cursorYear, :cursorSemester, :cursorId)
        order by sl.year desc, sl.semester desc, le.id desc
    """, nativeQuery = true
    )
    fun findByLectureIdLessThanOrderByDesc(lectureId: Long, cursorYear:Int, cursorSemester: Int, cursorId: Long, pageable: Pageable): List<Map<String, Any>>

    @Query("""
        select 1 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where sl.lecture_id = :lectureId and le.is_hidden = false 
        and (sl.year, sl.semester, le.id) < (:cursorYear, :cursorSemester, :cursorId) 
        limit 1
    """, nativeQuery = true
    )
    fun existsByLectureIdLessThan(lectureId: Long, cursorYear: Int, cursorSemester: Int, cursorId: Long): Int?

    @Query("""
        select new com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, sl.year, sl.semester, sl.lecture.id) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.rating) >= 4.0 
        ) 
        order by le.id desc
    """
    )
    fun findByLecturesRecommendedOrderByDesc(pageable: Pageable): List<LectureEvaluationWithSemester>

    @Query("""
        select new com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, sl.year, sl.semester, sl.lecture.id) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.rating) >= 4.0 
        ) 
        and le.id < :cursorId 
        order by le.id desc
    """
    )
    fun findByLecturesRecommendedLessThanOrderByDesc(cursorId: Long, pageable: Pageable): List<LectureEvaluationWithSemester>

    @Query("""
        select 1 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where le.is_hidden = false 
        and sl.lecture_id in ( 
            select sl1.lecture_id from lecture_evaluation le1 inner join semester_lecture sl1 on le1.semester_lecture_id = sl1.id and le1.is_hidden = false 
            group by sl1.lecture_id having avg(le1.rating) >= 4.0 
        ) 
        and le.id < :cursorId 
        limit 1
    """, nativeQuery = true
    )
    fun existsByLecturesRecommendedLessThan(cursorId: Long): Int?

    @Query("""
        select new com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, sl.year, sl.semester, sl.lecture.id) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.teachingSkill) >= 4.0 and avg(le1.gains) >= 4.0 
        ) 
        order by le.id desc
    """
    )
    fun findByLecturesFineOrderByDesc(pageable: Pageable): List<LectureEvaluationWithSemester>

    @Query("""
        select new com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, sl.year, sl.semester, sl.lecture.id) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.teachingSkill) >= 4.0 and avg(le1.gains) >= 4.0 
        ) 
        and le.id < :cursorId 
        order by le.id desc
    """
    )
    fun findByLecturesFineLessThanOrderByDesc(cursorId: Long, pageable: Pageable): List<LectureEvaluationWithSemester>

    @Query("""
        select 1 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where le.is_hidden = false 
        and sl.lecture_id in ( 
            select sl1.lecture_id from lecture_evaluation le1 inner join semester_lecture sl1 on le1.semester_lecture_id = sl1.id and le1.is_hidden = false 
            group by sl1.lecture_id having avg(le1.teaching_skill) >= 4.0 and avg(le1.gains) >= 4.0 
        ) 
        and le.id < :cursorId 
        limit 1
    """, nativeQuery = true
    )
    fun existsByLecturesFineLessThan(cursorId: Long): Int?

    @Query("""
        select new com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, sl.year, sl.semester, sl.lecture.id) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.gradeSatisfaction) >= 4.0 and avg(le1.lifeBalance) >= 4.0 
        ) 
        order by le.id desc
    """
    )
    fun findByLecturesHoneyOrderByDesc(pageable: Pageable): List<LectureEvaluationWithSemester>

    @Query("""
        select new com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, sl.year, sl.semester, sl.lecture.id) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.gradeSatisfaction) >= 4.0 and avg(le1.lifeBalance) >= 4.0 
        ) 
        and le.id < :cursorId 
        order by le.id desc
    """
    )
    fun findByLecturesHoneyLessThanOrderByDesc(cursorId: Long, pageable: Pageable): List<LectureEvaluationWithSemester>

    @Query("""
        select 1 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where le.is_hidden = false 
        and sl.lecture_id in ( 
            select sl1.lecture_id from lecture_evaluation le1 inner join semester_lecture sl1 on le1.semester_lecture_id = sl1.id and le1.is_hidden = false 
            group by sl1.lecture_id having avg(le1.grade_satisfaction) >= 4.0 and avg(le1.life_balance) >= 4.0 
        ) 
        and le.id < :cursorId 
        limit 1
    """, nativeQuery = true
    )
    fun existsByLecturesHoneyLessThan(cursorId: Long): Int?

    @Query("""
        select new com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, sl.year, sl.semester, sl.lecture.id) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.lifeBalance) < 2.0 and avg(le1.gains) >= 4.0 
        ) 
        order by le.id desc
    """
    )
    fun findByLecturesPainsGainsOrderByDesc(pageable: Pageable): List<LectureEvaluationWithSemester>

    @Query("""
        select new com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, sl.year, sl.semester, sl.lecture.id) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.lifeBalance) < 2.0 and avg(le1.gains) >= 4.0 
        ) 
        and le.id < :cursorId 
        order by le.id desc
    """
    )
    fun findByLecturesPainsGainsLessThanOrderByDesc(cursorId: Long, pageable: Pageable): List<LectureEvaluationWithSemester>

    @Query("""
        select 1 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where le.is_hidden = false 
        and sl.lecture_id in ( 
            select sl1.lecture_id from lecture_evaluation le1 inner join semester_lecture sl1 on le1.semester_lecture_id = sl1.id and le1.is_hidden = false 
            group by sl1.lecture_id having avg(le1.life_balance) < 2.0 and avg(le1.gains) >= 4.0 
        ) 
        and le.id < :cursorId 
        limit 1
    """, nativeQuery = true
    )
    fun existsByLecturesPainsGainsLessThan(cursorId: Long): Int?
}
