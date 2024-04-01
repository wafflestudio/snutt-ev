package com.wafflestudio.snuttev.core.domain.lecture.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.EnumPath
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.core.types.dsl.StringPath
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.snuttev.core.common.dto.SearchQueryDto
import com.wafflestudio.snuttev.core.common.type.LectureClassification
import com.wafflestudio.snuttev.core.domain.evaluation.model.QLectureEvaluation.lectureEvaluation
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureDto
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureEvaluationSimpleSummary
import com.wafflestudio.snuttev.core.domain.lecture.model.QLecture.lecture
import com.wafflestudio.snuttev.core.domain.lecture.model.QSemesterLecture.semesterLecture
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class LectureRepositoryImpl(private val queryFactory: JPAQueryFactory) : LectureRepositoryCustom {
    override fun searchLectures(request: SearchQueryDto, pageable: Pageable): Page<LectureDto> {
        val predicates = arrayOf(
            lecture.credit.isIn(request.credit),
            lecture.academicYear.isIn(request.academicYear),
            lecture.classification.isIn(request.classification),
            lecture.department.isIn(request.department),
            lecture.category.isIn(request.category),
            extractCriteriaFromQuery(request.query),
        )

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
            ),
        ).from(lecture)
            .leftJoin(lecture.semesterLectures, semesterLecture)
            .leftJoin(semesterLecture.evaluations, lectureEvaluation)
            .groupBy(lecture)
            .where(*predicates)
            .offset(pageable.offset).limit(pageable.pageSize.toLong()).fetch()

        val total = queryFactory.select(
            lecture.count(),
        ).from(lecture)
            .where(*predicates)
            .fetchOne()!!

        return PageImpl(queryResult, pageable, total)
    }

    override fun searchSemesterLectures(request: SearchQueryDto, pageable: Pageable): Page<LectureDto> {
        val predicates = arrayOf(
            request.yearSemesters.map { (year, semester) ->
                semesterLecture.year.eq(year).and(semesterLecture.semester.eq(semester))
            }.reduce { acc, next -> acc.or(next) },
            semesterLecture.credit.isIn(request.credit),
            semesterLecture.academicYear.isIn(request.academicYear),
            semesterLecture.classification.isIn(request.classification),
            semesterLecture.lecture.department.isIn(request.department),
            semesterLecture.category.isIn(request.category),
            extractCriteriaFromQuery(request.query),
        )

        val lectureSubQuery = queryFactory.selectFrom(lecture)
                .innerJoin(lecture.semesterLectures, semesterLecture)
                .where(*predicates)
                .offset(pageable.offset).limit(pageable.pageSize.toLong()).fetch()

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
            ),
        ).from(lecture)
            .innerJoin(lecture.semesterLectures, semesterLecture)
            .leftJoin(semesterLecture.evaluations, lectureEvaluation)
            .where(lecture.id.`in`(lectureSubQuery.map { it.id }))
            .groupBy(lecture)
            .fetch()

        val total = queryFactory.select(
            semesterLecture.count(),
        ).from(semesterLecture)
            .innerJoin(semesterLecture.lecture, lecture)
            .where(*predicates)
            .fetchOne()!!

        return PageImpl(queryResult, pageable, total)
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
        if (query.isNullOrBlank()) return builder.value
        query.split(' ').forEach { keyword ->
            // 소개원실 -> %소%개%원%실%
            val fuzzyKeyword = keyword.fold("%") { acc, c -> "$acc$c%" }
            val orBuilder = BooleanBuilder()
            when {
                keyword == "전공" -> orBuilder.or(
                    lecture.classification.`in`(
                        listOf(LectureClassification.ELECTIVE_SUBJECT, LectureClassification.REQUISITE_SUBJECT),
                    ),
                )
                keyword == "체육" -> orBuilder.or(lecture.category.eq("체육"))
                keyword in listOf("석박", "대학원") -> {
                    orBuilder.or(lecture.academicYear.`in`(listOf("석사", "박사", "석박사통합")))
                }
                keyword in listOf("학부", "학사") -> {
                    orBuilder.or(lecture.academicYear.notIn(listOf("석사", "박사", "석박사통합")))
                }
                keyword.hasKorean() -> {
                    orBuilder.or(lecture.title.like(fuzzyKeyword))
                    orBuilder.or(lecture.category.like(fuzzyKeyword))
                    orBuilder.or(lecture.instructor.eq(keyword))
                    orBuilder.or(lecture.academicYear.eq(keyword))
                    orBuilder.or(lecture.classification.eq(keyword))
                    when (keyword.last()) {
                        '과', '부' -> {
                            val keywordWithOutLastChar = fuzzyKeyword.substring(1, fuzzyKeyword.length - 2)
                            orBuilder.or(lecture.department.like(keywordWithOutLastChar))
                        }
                        '학' -> {}
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
        return this in '가'..'힣'
    }

    private fun String.hasKorean(): Boolean {
        return this.map { it.isHangul() }.reduce { acc, c -> acc || c }
    }
}
