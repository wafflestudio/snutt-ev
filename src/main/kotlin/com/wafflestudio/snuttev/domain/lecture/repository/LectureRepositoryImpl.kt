package com.wafflestudio.snuttev.domain.lecture.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.NumberPath
import com.querydsl.core.types.dsl.StringPath
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.snuttev.domain.evaluation.model.QLectureEvaluation.lectureEvaluation
import com.wafflestudio.snuttev.domain.lecture.dto.SearchLectureResponse
import com.wafflestudio.snuttev.domain.lecture.dto.SearchQuery
import com.wafflestudio.snuttev.domain.lecture.model.QLecture.lecture
import com.wafflestudio.snuttev.domain.lecture.model.QSemesterLecture.semesterLecture
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable


class LectureRepositoryImpl(private val queryFactory: JPAQueryFactory) : LectureRepositoryCustom {
    override fun searchLectures(request: SearchQuery, pageable: Pageable): Page<SearchLectureResponse> {
        val queryResult = queryFactory.select(
            Projections.constructor(
                SearchLectureResponse::class.java,
                lecture.id,
                lecture.classification,
                lecture.department,
                lecture.academicYear,
                lecture.courseNumber,
                lecture.title,
                lecture.credit,
                lecture.instructor,
                lecture.category,
                lectureEvaluation.rating.avg()
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

    override fun searchSemesterLectures(request: SearchQuery, pageable: Pageable): Page<SearchLectureResponse> {
        val queryResult =
            queryFactory.select(
                Projections.constructor(
                    SearchLectureResponse::class.java,
                    lecture.id,
                    semesterLecture.classification,
                    lecture.department,
                    semesterLecture.academicYear,
                    lecture.courseNumber,
                    lecture.title,
                    semesterLecture.credit,
                    lecture.instructor,
                    semesterLecture.category,
                    lectureEvaluation.rating.avg()
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

    private fun NumberPath<Int>.isIn(tags: List<Int>?): BooleanExpression? {
        return if (!tags.isNullOrEmpty()) this.`in`(tags) else null
    }

    private fun extractCriteriaFromQuery(query: String?): Predicate? {
        val builder = BooleanBuilder()
        query?.split(' ')?.forEach { keyword ->
            // 소개원실 -> %소%개%원%실%
            val fuzzyKeyword = keyword.fold("%") { acc, c -> "$acc$c%" }
            val orBuilder = BooleanBuilder()
            when {
                keyword == "전공" -> orBuilder.or(lecture.classification.`in`(listOf("전선", "전필")))
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
