package com.wafflestudio.snuttev.domain.evaluation.service

import com.wafflestudio.snuttev.domain.evaluation.dto.*
import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester
import com.wafflestudio.snuttev.domain.evaluation.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.error.LectureNotFoundException
import com.wafflestudio.snuttev.error.SemesterLectureNotFoundException
import com.wafflestudio.snuttev.error.WrongCursorFormatException
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.lang.NumberFormatException
import java.math.BigInteger

@Service
class EvaluationService(
    private val semesterLectureRepository: SemesterLectureRepository,
    private val lectureEvaluationRepository: LectureEvaluationRepository,
    private val lectureRepository: LectureRepository,
) {
    private val defaultPageSize = 20

    fun createEvaluation(
        userId: String,
        semesterLectureId: Long,
        createEvaluationRequest: CreateEvaluationRequest
    ): LectureEvaluationDto {
        val semesterLecture = semesterLectureRepository.findByIdOrNull(semesterLectureId) ?: throw SemesterLectureNotFoundException

        val lectureEvaluation = LectureEvaluation(
            semesterLecture = semesterLecture,
            userId = userId,
            content = createEvaluationRequest.content,
            gradeSatisfaction = createEvaluationRequest.gradeSatisfaction,
            teachingSkill = createEvaluationRequest.teachingSkill,
            gains = createEvaluationRequest.gains,
            lifeBalance = createEvaluationRequest.lifeBalance,
            rating = createEvaluationRequest.rating,
        )
        lectureEvaluationRepository.save(lectureEvaluation)
        return genLectureEvaluationDto(lectureEvaluation)
    }

    fun getEvaluationSummaryOfLecture(lectureId: Long): LectureEvaluationSummaryResponse {
        val lectureEvaluationSummaryDao = lectureRepository.findLectureWithAvgEvById(lectureId)
        if (lectureEvaluationSummaryDao.id == null) {
            throw LectureNotFoundException
        }

        return LectureEvaluationSummaryResponse(
            id = lectureEvaluationSummaryDao.id,
            title = lectureEvaluationSummaryDao.title,
            instructor = lectureEvaluationSummaryDao.instructor,
            department = lectureEvaluationSummaryDao.department,
            courseNumber = lectureEvaluationSummaryDao.courseNumber,
            credit = lectureEvaluationSummaryDao.credit,
            academicYear = lectureEvaluationSummaryDao.academicYear,
            category = lectureEvaluationSummaryDao.category,
            classification = lectureEvaluationSummaryDao.classification,
            summary = LectureEvaluationSummary(
                avgGradeSatisfaction = lectureEvaluationSummaryDao.avgGradeSatisfaction,
                avgTeachingSkill = lectureEvaluationSummaryDao.avgTeachingSkill,
                avgGains = lectureEvaluationSummaryDao.avgGains,
                avgLifeBalance = lectureEvaluationSummaryDao.avgLifeBalance,
                avgRating = lectureEvaluationSummaryDao.avgRating,
            ),
        )
    }

    fun getEvaluationsOfLecture(lectureId: Long, cursor: String?): CursorPaginationResponse {
        val pageable = PageRequest.of(0, defaultPageSize)
        val lectureEvaluationsCount = lectureEvaluationRepository.countByLectureId(lectureId)

        val lectureEvaluationsWithSemester = cursor?.let {
            val cursorValues = cursor.split("-")
            val cursorYear: Int
            val cursorSemester: Int
            val cursorId: Long
            try {
                cursorYear = cursorValues[0].toInt()
                cursorSemester = cursorValues[1].toInt()
                cursorId = cursorValues[2].toLong()
            } catch (e: IndexOutOfBoundsException) {
                throw WrongCursorFormatException
            } catch (e: NumberFormatException) {
                throw WrongCursorFormatException
            }

            lectureEvaluationRepository.findByLectureIdLessThanOrderByDesc(lectureId, cursorYear, cursorSemester, cursorId, pageable).map {
                LectureEvaluationWithSemester(
                    id = (it["id"] as BigInteger).toLong(),
                    userId = it["userId"] as String,
                    content = it["content"] as String,
                    gradeSatisfaction = it["gradeSatisfaction"] as Double,
                    teachingSkill = it["teachingSkill"] as Double,
                    gains = it["gains"] as Double,
                    lifeBalance = it["lifeBalance"] as Double,
                    rating = it["rating"] as Double,
                    likeCount = (it["likeCount"] as BigInteger).toLong(),
                    dislikeCount = (it["dislikeCount"] as BigInteger).toLong(),
                    isHidden = it["isHidden"] as Boolean,
                    isReported = it["isReported"] as Boolean,
                    year = it["year"] as Int,
                    semester = it["semester"] as Int,
                )
            }
        } ?: lectureEvaluationRepository.findByLectureIdOrderByDesc(lectureId, pageable)

        val lastLectureEvaluationWithSemester = lectureEvaluationsWithSemester.lastOrNull()

        val nextCursor = lastLectureEvaluationWithSemester?.let {
            "${it.year}-${it.semester}-${it.id}"
        }

        val isLast = lastLectureEvaluationWithSemester?.let {
            lectureEvaluationRepository.existsByLectureIdLessThan(lectureId, it.year, it.semester, it.id) == null
        } ?: true

        return CursorPaginationResponse(
            content = lectureEvaluationsWithSemester.map { genLectureEvaluationWithSemesterDto(it) },
            cursor = nextCursor,
            size = defaultPageSize,
            last = isLast,
            totalCount = lectureEvaluationsCount,
        )
    }

    private fun genLectureEvaluationDto(lectureEvaluation: LectureEvaluation): LectureEvaluationDto =
        LectureEvaluationDto(
            id = lectureEvaluation.id!!,
            userId = lectureEvaluation.userId,
            content = lectureEvaluation.content,
            gradeSatisfaction = lectureEvaluation.gradeSatisfaction,
            teachingSkill = lectureEvaluation.teachingSkill,
            gains = lectureEvaluation.gains,
            lifeBalance = lectureEvaluation.lifeBalance,
            rating = lectureEvaluation.rating,
            likeCount = lectureEvaluation.likeCount,
            dislikeCount = lectureEvaluation.dislikeCount,
            isHidden = lectureEvaluation.isHidden,
            isReported = lectureEvaluation.isReported,
        )

    private fun genLectureEvaluationWithSemesterDto(lectureEvaluationWithSemester: LectureEvaluationWithSemester): LectureEvaluationWithSemesterDto =
        LectureEvaluationWithSemesterDto(
            id = lectureEvaluationWithSemester.id,
            userId = lectureEvaluationWithSemester.userId,
            content = lectureEvaluationWithSemester.content,
            gradeSatisfaction = lectureEvaluationWithSemester.gradeSatisfaction,
            teachingSkill = lectureEvaluationWithSemester.teachingSkill,
            gains = lectureEvaluationWithSemester.gains,
            lifeBalance = lectureEvaluationWithSemester.lifeBalance,
            rating = lectureEvaluationWithSemester.rating,
            likeCount = lectureEvaluationWithSemester.likeCount,
            dislikeCount = lectureEvaluationWithSemester.dislikeCount,
            isHidden = lectureEvaluationWithSemester.isHidden,
            isReported = lectureEvaluationWithSemester.isReported,
            year = lectureEvaluationWithSemester.year,
            semester = lectureEvaluationWithSemester.semester,
        )
}
