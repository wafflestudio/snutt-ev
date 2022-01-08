package com.wafflestudio.snuttev.domain.evaluation.service

import com.wafflestudio.snuttev.domain.evaluation.dto.*
import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.domain.evaluation.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.error.LectureNotFoundException
import com.wafflestudio.snuttev.error.SemesterLectureNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class EvaluationService(
    private val semesterLectureRepository: SemesterLectureRepository,
    private val lectureEvaluationRepository: LectureEvaluationRepository,
    private val lectureRepository: LectureRepository,
) {
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
        val lectureEvaluationSummaryDao = lectureRepository.findLectureWithAvgEvById(lectureId) ?: throw LectureNotFoundException

        return LectureEvaluationSummaryResponse(
            id = lectureId,
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

    fun getEvaluationsOfLecture(lectureId: Long): LectureEvaluationsResponse {
        val lecture = lectureRepository.findByIdOrNull(lectureId) ?: throw LectureNotFoundException

        val lectureEvaluations = lecture.semesterLectures.flatMap { it.lectureEvaluations }
        return LectureEvaluationsResponse(
            lectureEvaluations.map { genLectureEvaluationDto(it) },
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

}
