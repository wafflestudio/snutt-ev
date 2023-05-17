package com.wafflestudio.snuttev.core.domain.evaluation.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.JPAExpressions.select
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.snuttev.core.common.type.LectureClassification
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationCursor
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationWithLectureDto
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationWithSemesterDto
import com.wafflestudio.snuttev.core.domain.evaluation.dto.QEvaluationWithLectureDto
import com.wafflestudio.snuttev.core.domain.evaluation.dto.QEvaluationWithSemesterDto
import com.wafflestudio.snuttev.core.domain.evaluation.model.QEvaluationLike.evaluationLike
import com.wafflestudio.snuttev.core.domain.evaluation.model.QLectureEvaluation.lectureEvaluation
import com.wafflestudio.snuttev.core.domain.lecture.model.QLecture.lecture
import com.wafflestudio.snuttev.core.domain.lecture.model.QSemesterLecture.semesterLecture
import com.wafflestudio.snuttev.core.domain.tag.model.Tag

class LectureEvaluationRepositoryImpl(private val queryFactory: JPAQueryFactory) : LectureEvaluationRepositoryCustom {
    override fun findEvaluationWithSemesterById(
        id: Long,
        userId: String
    ): EvaluationWithSemesterDto? = queryFactory
        .selectEvaluationWithSemesterDto(userId)
        .where(lectureEvaluation.id.eq(id))
        .where(lectureEvaluation.isHidden.isFalse)
        .fetchOne()

    override fun findNotMyEvaluationsWithSemesterByLectureId(
        lectureId: Long,
        userId: String,
        cursor: EvaluationCursor?,
        pageSize: Int
    ): List<EvaluationWithSemesterDto> = queryFactory
        .selectEvaluationWithSemesterDto(userId)
        .where(semesterLecture.lecture.id.eq(lectureId))
        .where(lectureEvaluation.userId.ne(userId))
        .where(lectureEvaluation.isHidden.isFalse)
        .where(getEvaluationCursorPredicate(cursor))
        .orderBy(semesterLecture.year.desc(), semesterLecture.semester.desc(), lectureEvaluation.id.desc())
        .limit(pageSize.toLong())
        .fetch()

    override fun findMyEvaluationsWithSemesterByLectureId(
        lectureId: Long,
        userId: String
    ): List<EvaluationWithSemesterDto> = queryFactory
        .selectEvaluationWithSemesterDto(userId)
        .where(semesterLecture.lecture.id.eq(lectureId))
        .where(lectureEvaluation.userId.eq(userId))
        .where(lectureEvaluation.isHidden.isFalse)
        .orderBy(semesterLecture.year.desc(), semesterLecture.semester.desc(), lectureEvaluation.id.desc())
        .fetch()

    override fun findMyEvaluationsWithLecture(
        userId: String,
        cursor: Long?,
        pageSize: Int
    ): List<EvaluationWithLectureDto> = queryFactory
        .selectEvaluationWithLectureDto(userId)
        .where(lectureEvaluation.userId.eq(userId))
        .where(lectureEvaluation.isHidden.isFalse)
        .where(getEvaluationIdCursorPredicate(cursor))
        .orderBy(lectureEvaluation.id.desc())
        .limit(pageSize.toLong())
        .fetch()

    override fun findEvaluationWithLectureByTag(
        userId: String,
        tag: Tag,
        cursor: Long?,
        pageSize: Int
    ): List<EvaluationWithLectureDto> = queryFactory
        .selectEvaluationWithLectureDto(userId)
        .where(getMainTagPredicate(tag))
        .where(lectureEvaluation.isHidden.isFalse)
        .where(getEvaluationIdCursorPredicate(cursor))
        .orderBy(lectureEvaluation.id.desc())
        .limit(pageSize.toLong())
        .fetch()

    private fun getMainTagPredicate(tag: Tag): BooleanBuilder {
        if (tag.name == "최신") {
            return BooleanBuilder()
        }

        val wherePredicate = BooleanBuilder(
            if (tag.name == "교양") {
                semesterLecture.classification.eq(LectureClassification.LIBERAL_EDUCATION)
            } else {
                null
            }
        )

        val havingPredicate = BooleanBuilder(
            when (tag.name) {
                "추천" -> lectureEvaluation.rating.avg().goe(4.0)
                "명강" -> lectureEvaluation.teachingSkill.avg().goe(4.0)
                    .and(lectureEvaluation.gains.avg().goe(4.0))
                "꿀강" -> lectureEvaluation.gradeSatisfaction.avg().goe(4.0)
                    .and(lectureEvaluation.lifeBalance.avg().goe(4.0))
                "고진감래" -> lectureEvaluation.lifeBalance.avg().lt(2.0)
                    .and(lectureEvaluation.gains.avg().goe(4.0))
                else -> null
            }
        )

        return BooleanBuilder(
            semesterLecture.lecture.id.`in`(
                select(semesterLecture.lecture.id)
                    .from(lectureEvaluation)
                    .innerJoin(lectureEvaluation.semesterLecture, semesterLecture)
                    .where(lectureEvaluation.isHidden.isFalse)
                    .where(wherePredicate)
                    .groupBy(semesterLecture.lecture.id)
                    .having(havingPredicate)
            )
        )
    }

    private fun JPAQueryFactory.selectEvaluationWithSemesterDto(userId: String) = select(
        QEvaluationWithSemesterDto(
            lectureEvaluation.id,
            lectureEvaluation.userId,
            lectureEvaluation.content,
            lectureEvaluation.gradeSatisfaction,
            lectureEvaluation.teachingSkill,
            lectureEvaluation.gains,
            lectureEvaluation.lifeBalance,
            lectureEvaluation.rating,
            lectureEvaluation.likeCount,
            lectureEvaluation.isHidden,
            lectureEvaluation.isReported,
            CaseBuilder().`when`(evaluationLike.id.isNull).then(false).otherwise(true).`as`("isLiked"),
            lectureEvaluation.fromSnuev,
            semesterLecture.year,
            semesterLecture.semester,
            semesterLecture.lecture.id
        )
    )
        .from(lectureEvaluation)
        .innerJoin(lectureEvaluation.semesterLecture, semesterLecture)
        .leftJoin(lectureEvaluation.evaluationLikes, evaluationLike).on(evaluationLike.userId.eq(userId))

    private fun JPAQueryFactory.selectEvaluationWithLectureDto(userId: String) = select(
        QEvaluationWithLectureDto(
            lectureEvaluation.id,
            lectureEvaluation.userId,
            lectureEvaluation.content,
            lectureEvaluation.gradeSatisfaction,
            lectureEvaluation.teachingSkill,
            lectureEvaluation.gains,
            lectureEvaluation.lifeBalance,
            lectureEvaluation.rating,
            lectureEvaluation.likeCount,
            lectureEvaluation.isHidden,
            lectureEvaluation.isReported,
            CaseBuilder().`when`(evaluationLike.id.isNull).then(false).otherwise(true).`as`("isLiked"),
            lectureEvaluation.fromSnuev,
            semesterLecture.year,
            semesterLecture.semester,
            semesterLecture.lecture.id,
            lecture.title,
            lecture.instructor
        )
    )
        .from(lectureEvaluation)
        .innerJoin(lectureEvaluation.semesterLecture, semesterLecture)
        .leftJoin(lectureEvaluation.evaluationLikes, evaluationLike).on(evaluationLike.userId.eq(userId))
        .innerJoin(semesterLecture.lecture, lecture)

    private fun getEvaluationCursorPredicate(cursor: EvaluationCursor?) = BooleanBuilder(
        cursor?.let {
            semesterLecture.year.lt(it.year)
                .or(semesterLecture.year.eq(it.year).and(semesterLecture.semester.lt(it.semester)))
                .or(
                    semesterLecture.year.eq(it.year).and(semesterLecture.semester.eq(it.semester))
                        .and(lectureEvaluation.id.lt(it.lectureEvaluationId))
                )
        }
    )

    private fun getEvaluationIdCursorPredicate(cursor: Long?) = BooleanBuilder(
        cursor?.let {
            lectureEvaluation.id.lt(it)
        }
    )
}
