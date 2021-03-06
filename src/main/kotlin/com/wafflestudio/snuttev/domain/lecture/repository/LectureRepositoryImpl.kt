package com.wafflestudio.snuttev.domain.lecture.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.EnumPath
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.core.types.dsl.StringPath
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.snuttev.domain.evaluation.model.QLectureEvaluation.lectureEvaluation
import com.wafflestudio.snuttev.domain.lecture.dto.LectureDto
import com.wafflestudio.snuttev.domain.lecture.dto.LectureEvaluationSimpleSummary
import com.wafflestudio.snuttev.domain.lecture.model.LectureClassification
import com.wafflestudio.snuttev.domain.lecture.model.QLecture.lecture
import com.wafflestudio.snuttev.domain.lecture.model.QSemesterLecture.semesterLecture
import com.wafflestudio.snuttev.domain.lecture.service.SearchQueryDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable


class LectureRepositoryImpl(private val queryFactory: JPAQueryFactory) : LectureRepositoryCustom {
    override fun searchLectures(request: SearchQueryDto, pageable: Pageable): Page<LectureDto> {
        val queryResult = queryFactory.select(
            Projections.constructor(
                LectureDto::class.java,
                lecture.id,
                lecture.title,
                lecture.instructor,
                lecture.department,
                lecture.courseNumber,
                lecture.credit,
                lecture.academicYear,
                lecture.category,
                lecture.classification,
                Projections.constructor(
                    LectureEvaluationSimpleSummary::class.java,
                    lectureEvaluation.rating.avg(),
                ),
            )
        ).from(lecture)
            .leftJoin(lecture.semesterLectures, semesterLecture)
            .leftJoin(semesterLecture.evaluations, lectureEvaluation)
            .groupBy(lecture)
            .where(
                lecture.credit.isIn(request.credit),
                lecture.academicYear.isIn(request.academicYear),
                lecture.classification.isIn(request.classification),
                lecture.department.isIn(request.department),
                lecture.category.isIn(request.category),
                extractCriteriaFromQuery(request.query)
            ).offset(pageable.offset).limit(pageable.pageSize.toLong()).fetchResults()
        val content = queryResult.results
        val total: Long = queryResult.total

        return PageImpl(content, pageable, total)
    }

    override fun searchSemesterLectures(request: SearchQueryDto, pageable: Pageable): Page<LectureDto> {
        val queryResult =
            queryFactory.select(
                Projections.constructor(
                    LectureDto::class.java,
                    lecture.id,
                    lecture.title,
                    lecture.instructor,
                    lecture.department,
                    lecture.courseNumber,
                    lecture.credit,
                    lecture.academicYear,
                    lecture.category,
                    lecture.classification,
                    Projections.constructor(
                        LectureEvaluationSimpleSummary::class.java,
                        lectureEvaluation.rating.avg(),
                    ),
                )
            ).from(semesterLecture)
                .innerJoin(semesterLecture.lecture, lecture)
                .leftJoin(semesterLecture.evaluations, lectureEvaluation)
                .groupBy(lecture)
                .where(
                    request.year?.let { semesterLecture.year.eq(it) },
                    request.semester?.let { semesterLecture.semester.eq(it) },
                    semesterLecture.credit.isIn(request.credit),
                    semesterLecture.academicYear.isIn(request.academicYear),
                    semesterLecture.classification.isIn(request.classification),
                    semesterLecture.lecture.department.isIn(request.department),
                    semesterLecture.category.isIn(request.category),
                    extractCriteriaFromQuery(request.query)
                ).offset(pageable.offset).limit(pageable.pageSize.toLong()).fetchResults()
        val content = queryResult.results
        val total: Long = queryResult.total

        return PageImpl(content, pageable, total)
    }

    private fun StringPath.isIn(tags: List<String>?): BooleanExpression? {
        return if (!tags.isNullOrEmpty()) this.`in`(tags) else null
    }

    private fun EnumPath<LectureClassification>.isIn(tags: List<String>?): BooleanExpression? {
        return if (!tags.isNullOrEmpty()) this.`in`(tags.map { LectureClassification.customValueOf(it) }) else null
    }

    private fun EnumPath<LectureClassification>.eq(keyword: String): BooleanExpression? {
        val keywordLectureClassification = LectureClassification.customValueOf(keyword)
        return if (keywordLectureClassification != null) this.eq(keywordLectureClassification) else null
    }

    private fun NumberPath<Int>.isIn(tags: List<Int>?): BooleanExpression? {
        return if (!tags.isNullOrEmpty()) this.`in`(tags) else null
    }

    private fun extractCriteriaFromQuery(query: String?): Predicate? {
        val builder = BooleanBuilder()
        if(query.isNullOrBlank()) return builder.value
        query.split(' ').forEach { keyword ->
            // ???????????? -> %???%???%???%???%
            val fuzzyKeyword = keyword.fold("%") { acc, c -> "$acc$c%" }
            val orBuilder = BooleanBuilder()
            when {
                keyword == "??????" -> orBuilder.or(lecture.classification.`in`(
                    listOf(LectureClassification.ELECTIVE_SUBJECT, LectureClassification.REQUISITE_SUBJECT)
                ))
                keyword == "??????" -> orBuilder.or(lecture.category.eq("??????"))
                keyword in listOf("??????", "?????????") -> {
                    orBuilder.or(lecture.academicYear.`in`(listOf("??????", "??????", "???????????????")))
                }
                keyword in listOf("??????", "??????") -> {
                    orBuilder.or(lecture.academicYear.notIn(listOf("??????", "??????", "???????????????")))
                }
                keyword.hasKorean() -> {
                    orBuilder.or(lecture.title.like(fuzzyKeyword))
                    orBuilder.or(lecture.category.like(fuzzyKeyword))
                    orBuilder.or(lecture.instructor.eq(keyword))
                    orBuilder.or(lecture.academicYear.eq(keyword))
                    orBuilder.or(lecture.classification.eq(keyword))
                    when (keyword.last()) {
                        '???', '???' -> {
                            val keywordWithOutLastChar = fuzzyKeyword.substring(1, fuzzyKeyword.length - 2)
                            orBuilder.or(lecture.department.like(keywordWithOutLastChar))
                        }
                        '???' -> {}
                        else -> orBuilder.or(lecture.department.like(fuzzyKeyword.substring(1)))
                    }
                }
                else -> {
                    orBuilder.or(lecture.title.like("%$keyword%"))
                    orBuilder.or(lecture.instructor.like("%$keyword%"))
                    orBuilder.or(lecture.courseNumber.like(keyword))
                }
            }
            builder.and(orBuilder.value)
        }
        return builder.value
    }

    private fun Char.isHangul(): Boolean {
        return this in '???'..'???'
    }

    private fun String.hasKorean(): Boolean {
        return this.map { it.isHangul() }.reduce { acc, c -> acc || c }
    }
}
