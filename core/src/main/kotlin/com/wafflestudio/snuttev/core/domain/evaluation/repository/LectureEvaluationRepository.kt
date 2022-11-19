package com.wafflestudio.snuttev.core.domain.evaluation.repository

import com.wafflestudio.snuttev.core.common.type.LectureClassification
import com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LectureEvaluationRepository : JpaRepository<LectureEvaluation, Long> {

    fun findByIdAndIsHiddenFalse(id: Long): LectureEvaluation?

    fun existsBySemesterLectureIdAndUserIdAndIsHiddenFalse(semesterLectureId: Long, userId: String): Boolean

    @Query("select count(le.id) from LectureEvaluation le inner join le.semesterLecture sl where sl.lecture.id = :lectureId and le.isHidden = false")
    fun countByLectureId(lectureId: Long): Long

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, cast(null as string), cast(null as string)) 
        from LectureEvaluation le inner join le.semesterLecture sl where sl.lecture.id = :lectureId and le.isHidden = false and le.userId <> :userId 
        order by sl.year desc, sl.semester desc, le.id desc
    """
    )
    fun findByLectureIdOrderByDesc(lectureId: Long, userId: String, pageable: Pageable): List<LectureEvaluationWithLecture>

    @Query(
        """
        select 
        le.id as id, le.user_id as userId, le.content as content, le.grade_satisfaction as gradeSatisfaction, le.teaching_skill as teachingSkill, le.gains as gains, le.life_balance as lifeBalance, le.rating as rating, 
        le.like_count as likeCount, le.dislike_count as dislikeCount, le.is_hidden as isHidden, le.is_reported as isReported, le.from_snuev as fromSnuev, sl.year as year, sl.semester as semester, sl.lecture_id as lectureId, cast(null as char) as lectureTitle, cast(null as char) as lectureInstructor 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where sl.lecture_id = :lectureId and le.is_hidden = false and le.user_id <> :userId 
        and (sl.year, sl.semester, le.id) < (:cursorYear, :cursorSemester, :cursorId)
        order by sl.year desc, sl.semester desc, le.id desc
    """,
        nativeQuery = true
    )
    fun findByLectureIdLessThanOrderByDesc(lectureId: Long, userId: String, cursorYear: Int, cursorSemester: Int, cursorId: Long, pageable: Pageable): List<Map<String, Any>>

    @Query(
        """
        select 1 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where sl.lecture_id = :lectureId and le.is_hidden = false and le.user_id <> :userId 
        and (sl.year, sl.semester, le.id) < (:cursorYear, :cursorSemester, :cursorId) 
        limit 1
    """,
        nativeQuery = true
    )
    fun existsByLectureIdLessThan(lectureId: Long, userId: String, cursorYear: Int, cursorSemester: Int, cursorId: Long): Int?

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, sl.lecture.title, sl.lecture.instructor) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.classification = :classification 
        order by le.id desc
    """
    )
    fun findByLecturesRecentOrderByDesc(classification: LectureClassification, pageable: Pageable): List<LectureEvaluationWithLecture>

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, sl.lecture.title, sl.lecture.instructor) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.classification = :classification 
        and le.id < :cursorId 
        order by le.id desc
    """
    )
    fun findByLecturesRecentLessThanOrderByDesc(classification: LectureClassification, cursorId: Long, pageable: Pageable): List<LectureEvaluationWithLecture>

    @Query(
        """
        select 1 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where le.is_hidden = false 
        and sl.classification = :#{#classification.value} 
        and le.id < :cursorId 
        limit 1
    """,
        nativeQuery = true
    )
    fun existsByLecturesRecentLessThan(classification: LectureClassification, cursorId: Long): Int?

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, sl.lecture.title, sl.lecture.instructor) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.classification = :classification 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.rating) >= 4.0 
        ) 
        order by le.id desc
    """
    )
    fun findByLecturesRecommendedOrderByDesc(classification: LectureClassification, pageable: Pageable): List<LectureEvaluationWithLecture>

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, sl.lecture.title, sl.lecture.instructor) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.classification = :classification 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.rating) >= 4.0 
        ) 
        and le.id < :cursorId 
        order by le.id desc
    """
    )
    fun findByLecturesRecommendedLessThanOrderByDesc(classification: LectureClassification, cursorId: Long, pageable: Pageable): List<LectureEvaluationWithLecture>

    @Query(
        """
        select 1 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where le.is_hidden = false 
        and sl.classification = :#{#classification.value} 
        and sl.lecture_id in ( 
            select sl1.lecture_id from lecture_evaluation le1 inner join semester_lecture sl1 on le1.semester_lecture_id = sl1.id and le1.is_hidden = false 
            group by sl1.lecture_id having avg(le1.rating) >= 4.0 
        ) 
        and le.id < :cursorId 
        limit 1
    """,
        nativeQuery = true
    )
    fun existsByLecturesRecommendedLessThan(classification: LectureClassification, cursorId: Long): Int?

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, sl.lecture.title, sl.lecture.instructor) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.classification = :classification 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.teachingSkill) >= 4.0 and avg(le1.gains) >= 4.0 
        ) 
        order by le.id desc
    """
    )
    fun findByLecturesFineOrderByDesc(classification: LectureClassification, pageable: Pageable): List<LectureEvaluationWithLecture>

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, sl.lecture.title, sl.lecture.instructor) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.classification = :classification 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.teachingSkill) >= 4.0 and avg(le1.gains) >= 4.0 
        ) 
        and le.id < :cursorId 
        order by le.id desc
    """
    )
    fun findByLecturesFineLessThanOrderByDesc(classification: LectureClassification, cursorId: Long, pageable: Pageable): List<LectureEvaluationWithLecture>

    @Query(
        """
        select 1 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where le.is_hidden = false 
        and sl.classification = :#{#classification.value} 
        and sl.lecture_id in ( 
            select sl1.lecture_id from lecture_evaluation le1 inner join semester_lecture sl1 on le1.semester_lecture_id = sl1.id and le1.is_hidden = false 
            group by sl1.lecture_id having avg(le1.teaching_skill) >= 4.0 and avg(le1.gains) >= 4.0 
        ) 
        and le.id < :cursorId 
        limit 1
    """,
        nativeQuery = true
    )
    fun existsByLecturesFineLessThan(classification: LectureClassification, cursorId: Long): Int?

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, sl.lecture.title, sl.lecture.instructor) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.classification = :classification 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.gradeSatisfaction) >= 4.0 and avg(le1.lifeBalance) >= 4.0 
        ) 
        order by le.id desc
    """
    )
    fun findByLecturesHoneyOrderByDesc(classification: LectureClassification, pageable: Pageable): List<LectureEvaluationWithLecture>

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, sl.lecture.title, sl.lecture.instructor) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.classification = :classification 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.gradeSatisfaction) >= 4.0 and avg(le1.lifeBalance) >= 4.0 
        ) 
        and le.id < :cursorId 
        order by le.id desc
    """
    )
    fun findByLecturesHoneyLessThanOrderByDesc(classification: LectureClassification, cursorId: Long, pageable: Pageable): List<LectureEvaluationWithLecture>

    @Query(
        """
        select 1 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where le.is_hidden = false 
        and sl.classification = :#{#classification.value} 
        and sl.lecture_id in ( 
            select sl1.lecture_id from lecture_evaluation le1 inner join semester_lecture sl1 on le1.semester_lecture_id = sl1.id and le1.is_hidden = false 
            group by sl1.lecture_id having avg(le1.grade_satisfaction) >= 4.0 and avg(le1.life_balance) >= 4.0 
        ) 
        and le.id < :cursorId 
        limit 1
    """,
        nativeQuery = true
    )
    fun existsByLecturesHoneyLessThan(classification: LectureClassification, cursorId: Long): Int?

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, sl.lecture.title, sl.lecture.instructor) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.classification = :classification 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.lifeBalance) < 2.0 and avg(le1.gains) >= 4.0 
        ) 
        order by le.id desc
    """
    )
    fun findByLecturesPainsGainsOrderByDesc(classification: LectureClassification, pageable: Pageable): List<LectureEvaluationWithLecture>

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, sl.lecture.title, sl.lecture.instructor) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.isHidden = false 
        and sl.classification = :classification 
        and sl.lecture.id in ( 
            select sl1.lecture.id from LectureEvaluation le1 inner join SemesterLecture sl1 on le1.semesterLecture.id = sl1.id and le1.isHidden = false 
            group by sl1.lecture.id having avg(le1.lifeBalance) < 2.0 and avg(le1.gains) >= 4.0 
        ) 
        and le.id < :cursorId 
        order by le.id desc
    """
    )
    fun findByLecturesPainsGainsLessThanOrderByDesc(classification: LectureClassification, cursorId: Long, pageable: Pageable): List<LectureEvaluationWithLecture>

    @Query(
        """
        select 1 
        from lecture_evaluation le inner join semester_lecture sl on le.semester_lecture_id = sl.id where le.is_hidden = false 
        and sl.classification = :#{#classification.value} 
        and sl.lecture_id in ( 
            select sl1.lecture_id from lecture_evaluation le1 inner join semester_lecture sl1 on le1.semester_lecture_id = sl1.id and le1.is_hidden = false 
            group by sl1.lecture_id having avg(le1.life_balance) < 2.0 and avg(le1.gains) >= 4.0 
        ) 
        and le.id < :cursorId 
        limit 1
    """,
        nativeQuery = true
    )
    fun existsByLecturesPainsGainsLessThan(classification: LectureClassification, cursorId: Long): Int?

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, cast(null as string), cast(null as string)) 
        from LectureEvaluation le inner join le.semesterLecture sl where sl.lecture.id = :lectureId and le.userId = :userId and le.isHidden = false 
        order by sl.year desc, sl.semester desc, le.id desc
    """
    )
    fun findByLectureIdAndUserIdOrderByDesc(lectureId: Long, userId: String): List<LectureEvaluationWithLecture>

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, sl.lecture.title, sl.lecture.instructor) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.userId = :userId and le.isHidden = false
        order by le.id desc
    """
    )
    fun findByUserIdOrderByDesc(userId: String, pageable: Pageable): List<LectureEvaluationWithLecture>

    @Query(
        """
        select new com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluationWithLecture(
        le.id, le.userId, le.content, le.gradeSatisfaction, le.teachingSkill, le.gains, le.lifeBalance, le.rating, 
        le.likeCount, le.dislikeCount, le.isHidden, le.isReported, le.fromSnuev, sl.year, sl.semester, sl.lecture.id, sl.lecture.title, sl.lecture.instructor) 
        from LectureEvaluation le inner join le.semesterLecture sl where le.userId = :userId and le.isHidden = false 
        and le.id < :cursorId 
        order by le.id desc
    """
    )
    fun findByUserIdLessThanOrderByDesc(userId: String, cursorId: Long, pageable: Pageable): List<LectureEvaluationWithLecture>

    fun countByUserIdAndIsHiddenFalse(userId: String): Long

    @Query("select le.semesterLecture.lecture.id from LectureEvaluation le where le.userId = :userId and le.isHidden = false ")
    fun findLectureIdsByLectureEvaluationUserId(userId: String): List<Long>
}
