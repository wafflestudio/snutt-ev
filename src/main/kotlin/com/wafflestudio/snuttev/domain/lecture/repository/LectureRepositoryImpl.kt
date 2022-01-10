package com.wafflestudio.snuttev.domain.lecture.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import com.wafflestudio.snuttev.domain.lecture.dto.SearchLectureRequest
import com.wafflestudio.snuttev.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.domain.lecture.model.QLecture.lecture
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable


class LectureRepositoryImpl(private val queryFactory: JPAQueryFactory) : LectureRepositoryCustom {
    override fun searchLectures(request: SearchLectureRequest, pageable: Pageable): Page<Lecture> {
        val builder = BooleanBuilder()
        if (!request.credit.isNullOrEmpty()) builder.and(lecture.credit.`in`(request.credit))
        if (!request.instructor.isNullOrEmpty()) builder.and(lecture.instructor.`in`(request.instructor))
        if (!request.academicYear.isNullOrEmpty()) builder.and(lecture.academicYear.`in`(request.academicYear))
        if (!request.classification.isNullOrEmpty()) builder.and(lecture.classification.`in`(request.classification))
        if (!request.category.isNullOrEmpty()) builder.and(lecture.category.`in`(request.category))
        if (!request.department.isNullOrEmpty()) builder.and(lecture.department.`in`(request.department))
        request.query?.split(' ')?.forEach { keyword ->
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
        val queryResult = queryFactory.selectFrom(lecture).where(builder.value)
            .offset(pageable.offset).limit(pageable.pageSize.toLong()).fetchResults()
        val content = queryResult.results
        val total: Long = queryResult.total

        return PageImpl(content, pageable, total)
    }

    private fun Char.isHangul(): Boolean {
        return this in '가'..'힣'
    }

    private fun String.hasKorean(): Boolean {
        return this.map { it.isHangul() }.reduce { acc, c -> acc || c }
    }
}