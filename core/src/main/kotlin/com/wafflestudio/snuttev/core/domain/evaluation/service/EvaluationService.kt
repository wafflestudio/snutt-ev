package com.wafflestudio.snuttev.core.domain.evaluation.service

import com.wafflestudio.snuttev.core.common.dto.common.CursorPaginationResponse
import com.wafflestudio.snuttev.core.common.error.EvaluationAlreadyExistsException
import com.wafflestudio.snuttev.core.common.error.EvaluationLikeAlreadyExistsException
import com.wafflestudio.snuttev.core.common.error.EvaluationLikeAlreadyNotExistsException
import com.wafflestudio.snuttev.core.common.error.EvaluationReportAlreadyExistsException
import com.wafflestudio.snuttev.core.common.error.LectureEvaluationNotFoundException
import com.wafflestudio.snuttev.core.common.error.LectureNotFoundException
import com.wafflestudio.snuttev.core.common.error.MyLectureEvaluationException
import com.wafflestudio.snuttev.core.common.error.NotMyLectureEvaluationException
import com.wafflestudio.snuttev.core.common.error.SemesterLectureNotFoundException
import com.wafflestudio.snuttev.core.common.error.TagNotFoundException
import com.wafflestudio.snuttev.core.common.util.PageUtils
import com.wafflestudio.snuttev.core.common.util.cache.Cache
import com.wafflestudio.snuttev.core.common.util.cache.CacheKey
import com.wafflestudio.snuttev.core.domain.evaluation.dto.*
import com.wafflestudio.snuttev.core.domain.evaluation.model.EvaluationLike
import com.wafflestudio.snuttev.core.domain.evaluation.model.EvaluationReport
import com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.core.domain.evaluation.repository.EvaluationLikeRepository
import com.wafflestudio.snuttev.core.domain.evaluation.repository.EvaluationReportRepository
import com.wafflestudio.snuttev.core.domain.evaluation.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.core.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.core.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.core.domain.tag.repository.TagRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EvaluationService internal constructor(
    private val semesterLectureRepository: SemesterLectureRepository,
    private val lectureEvaluationRepository: LectureEvaluationRepository,
    private val lectureRepository: LectureRepository,
    private val tagRepository: TagRepository,
    private val evaluationReportRepository: EvaluationReportRepository,
    private val evaluationLikeRepository: EvaluationLikeRepository,
    private val cache: Cache,
) {
    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }

    fun createEvaluation(
        userId: String,
        semesterLectureId: Long,
        createEvaluationRequest: CreateEvaluationRequest
    ): LectureEvaluationDto {
        val semesterLecture = getSemesterLectureToWriteEvaluation(semesterLectureId, userId)

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

        cache.deleteAll(CacheKey.EVALUATIONS_BY_TAG_PAGE)

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
            evaluation = LectureEvaluationSummary(
                avgGradeSatisfaction = lectureEvaluationSummaryDao.avgGradeSatisfaction,
                avgTeachingSkill = lectureEvaluationSummaryDao.avgTeachingSkill,
                avgGains = lectureEvaluationSummaryDao.avgGains,
                avgLifeBalance = lectureEvaluationSummaryDao.avgLifeBalance,
                avgRating = lectureEvaluationSummaryDao.avgRating,
            ),
        )
    }

    fun getEvaluationsOfLecture(
        userId: String,
        lectureId: Long,
        cursor: String?,
    ): CursorPaginationResponse<EvaluationWithSemesterResponse> {
        val evaluationCursor = PageUtils.getCursor<EvaluationCursor>(cursor)

        val lectureEvaluationsCount = lectureEvaluationRepository.countByLectureId(lectureId)

        var evaluationWithSemesterDtos = lectureEvaluationRepository.findNotMyEvaluationsWithSemesterByLectureId(
            lectureId,
            userId,
            evaluationCursor,
            DEFAULT_PAGE_SIZE + 1,
        )

        var nextCursor: String? = null
        if (evaluationWithSemesterDtos.size > DEFAULT_PAGE_SIZE) {
            evaluationWithSemesterDtos = evaluationWithSemesterDtos.dropLast(1)
            evaluationWithSemesterDtos.last().run {
                nextCursor = PageUtils.generateCursor(EvaluationCursor(this.year, this.semester, this.id))
            }
        }

        return CursorPaginationResponse(
            content = evaluationWithSemesterDtos.map { EvaluationWithSemesterResponse.of(it, userId) },
            cursor = nextCursor,
            size = DEFAULT_PAGE_SIZE,
            last = nextCursor == null,
            totalCount = lectureEvaluationsCount,
        )
    }

    fun getMyEvaluationsOfLecture(userId: String, lectureId: Long): EvaluationsResponse {
        val evaluationWithSemesterDtos = lectureEvaluationRepository.findMyEvaluationsWithSemesterByLectureId(
            lectureId, userId,
        )
        return EvaluationsResponse(
            evaluations = evaluationWithSemesterDtos.map { EvaluationWithSemesterResponse.of(it, userId) },
        )
    }

    fun getMyEvaluations(userId: String, cursor: String?): CursorPaginationResponse<EvaluationWithLectureResponse> {
        val evaluationIdCursor = PageUtils.getCursor<Long>(cursor)

        val lectureEvaluationsCount = lectureEvaluationRepository.countByUserIdAndIsHiddenFalse(userId)

        var evaluationWithLectureDtos = lectureEvaluationRepository.findMyEvaluationsWithLecture(
            userId,
            evaluationIdCursor,
            DEFAULT_PAGE_SIZE + 1,
        )

        var nextCursor: String? = null
        if (evaluationWithLectureDtos.size > DEFAULT_PAGE_SIZE) {
            evaluationWithLectureDtos = evaluationWithLectureDtos.dropLast(1)
            evaluationWithLectureDtos.last().run {
                nextCursor = PageUtils.generateCursor(this.id)
            }
        }

        return CursorPaginationResponse(
            content = evaluationWithLectureDtos.map { it.toResponse(userId) },
            cursor = nextCursor,
            size = DEFAULT_PAGE_SIZE,
            last = nextCursor == null,
            totalCount = lectureEvaluationsCount,
        )
    }

    fun getMainTagEvaluations(
        userId: String,
        tagId: Long,
        cursor: String?,
    ): CursorPaginationResponse<EvaluationWithLectureResponse> {
        val evaluationIdCursor = PageUtils.getCursor<Long>(cursor)

        var evaluationWithLectureDtos = cache.withCache(
            builtCacheKey = CacheKey.EVALUATIONS_BY_TAG_PAGE.build(
                tagId, evaluationIdCursor, DEFAULT_PAGE_SIZE + 1,
            ),
            postHitProcessor = { dtos ->
                val evaluationsIds = dtos.map { it.id }
                val likes = evaluationLikeRepository.findAllByLectureEvaluationIdIn(evaluationsIds)
                dtos.map { dto ->
                    dto.copy(
                        likeCount = likes.count { it.lectureEvaluation.id == dto.id }.toLong(),
                        isLiked = likes.any { it.lectureEvaluation.id == dto.id && it.userId == userId },
                    )
                }
            },
        ) {
            val tag = tagRepository.findByIdOrNull(tagId) ?: throw TagNotFoundException
            lectureEvaluationRepository.findEvaluationWithLectureByTag(
                userId,
                tag,
                evaluationIdCursor,
                DEFAULT_PAGE_SIZE + 1,
            )
        } ?: emptyList()

        var nextCursor: String? = null
        if (evaluationWithLectureDtos.size > DEFAULT_PAGE_SIZE) {
            evaluationWithLectureDtos = evaluationWithLectureDtos.dropLast(1)
            evaluationWithLectureDtos.last().run {
                nextCursor = PageUtils.generateCursor(this.id)
            }
        }

        return CursorPaginationResponse(
            content = evaluationWithLectureDtos.map { it.toResponse(userId) },
            cursor = nextCursor,
            size = DEFAULT_PAGE_SIZE,
            last = nextCursor == null,
        )
    }

    fun getEvaluation(
        userId: String,
        evaluationId: Long,
    ): EvaluationWithSemesterResponse {
        return lectureEvaluationRepository.findEvaluationWithSemesterById(evaluationId, userId)
            ?.let { EvaluationWithSemesterResponse.of(it, userId) }
            ?: throw LectureEvaluationNotFoundException
    }

    @Transactional
    fun updateEvaluation(
        userId: String,
        evaluationId: Long,
        updateEvaluationRequest: UpdateEvaluationRequest,
    ): EvaluationWithSemesterResponse {
        val evaluation = lectureEvaluationRepository.findByIdAndIsHiddenFalse(evaluationId)
            ?: throw LectureEvaluationNotFoundException
        if (evaluation.userId != userId) {
            throw NotMyLectureEvaluationException
        }

        updateEvaluationRequest.run {
            content?.let {
                if (it.isBlank()) throw IllegalArgumentException("내용이 비어있습니다.")
                evaluation.content = it
            }

            gradeSatisfaction?.let { evaluation.gradeSatisfaction = it }
            teachingSkill?.let { evaluation.teachingSkill = it }
            gains?.let { evaluation.gains = it }
            lifeBalance?.let { evaluation.lifeBalance = it }
            rating?.let { evaluation.rating = it }

            semesterLectureId?.let {
                if (it != evaluation.semesterLecture.id) {
                    evaluation.semesterLecture = getSemesterLectureToWriteEvaluation(it, userId)
                }
            }
        }

        cache.deleteAll(CacheKey.EVALUATIONS_BY_TAG_PAGE)

        val isLiked = evaluationLikeRepository.existsByLectureEvaluationAndUserId(evaluation, userId)
        return EvaluationWithSemesterResponse.of(evaluation, userId, isLiked)
    }

    private fun getSemesterLectureToWriteEvaluation(
        semesterLectureId: Long,
        userId: String,
    ): SemesterLecture {
        val semesterLecture = semesterLectureRepository.findByIdOrNull(semesterLectureId)
            ?: throw SemesterLectureNotFoundException

        if (lectureEvaluationRepository.existsBySemesterLectureAndUserIdAndIsHiddenFalse(semesterLecture, userId))
            throw EvaluationAlreadyExistsException

        return semesterLecture
    }

    @Transactional
    fun deleteEvaluation(
        userId: String,
        evaluationId: Long,
    ) {
        val lectureEvaluation = lectureEvaluationRepository.findByIdAndIsHiddenFalse(evaluationId) ?: throw LectureEvaluationNotFoundException
        if (lectureEvaluation.userId != userId) {
            throw NotMyLectureEvaluationException
        }

        lectureEvaluation.isHidden = true

        cache.deleteAll(CacheKey.EVALUATIONS_BY_TAG_PAGE)
    }

    fun reportEvaluation(
        userId: String,
        evaluationId: Long,
        createEvaluationReportRequest: CreateEvaluationReportRequest,
    ): EvaluationReportDto {
        val lectureEvaluation = lectureEvaluationRepository.findByIdAndIsHiddenFalse(evaluationId) ?: throw LectureEvaluationNotFoundException
        if (lectureEvaluation.userId == userId) {
            throw MyLectureEvaluationException
        }

        if (evaluationReportRepository.existsByLectureEvaluationIdAndUserId(evaluationId, userId)) {
            throw EvaluationReportAlreadyExistsException
        }

        val evaluationReport = EvaluationReport(
            lectureEvaluation = lectureEvaluation,
            userId = userId,
            content = createEvaluationReportRequest.content,
        )
        evaluationReportRepository.save(evaluationReport)
        return genEvaluationReportDto(evaluationReport)
    }

    @Transactional
    fun likeEvaluation(
        userId: String,
        evaluationId: Long,
    ) {
        val evaluation = lectureEvaluationRepository.findByIdAndIsHiddenFalse(evaluationId) ?: throw LectureEvaluationNotFoundException

        try {
            evaluationLikeRepository.save(
                EvaluationLike(
                    lectureEvaluation = evaluation,
                    userId = userId,
                )
            )
        } catch (e: DataIntegrityViolationException) {
            throw EvaluationLikeAlreadyExistsException
        }

        evaluation.likeCount++
    }

    @Transactional
    fun cancelLikeEvaluation(
        userId: String,
        evaluationId: Long,
    ) {
        val evaluation = lectureEvaluationRepository.findByIdAndIsHiddenFalse(evaluationId) ?: throw LectureEvaluationNotFoundException

        val deletedRowCount = evaluationLikeRepository.deleteByLectureEvaluationAndUserId(evaluation, userId)
        if (deletedRowCount == 0L) throw EvaluationLikeAlreadyNotExistsException

        evaluation.likeCount--
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
            isHidden = lectureEvaluation.isHidden,
            isReported = lectureEvaluation.isReported,
            fromSnuev = lectureEvaluation.fromSnuev,
        )

    private fun genEvaluationReportDto(evaluationReport: EvaluationReport): EvaluationReportDto =
        EvaluationReportDto(
            id = evaluationReport.id!!,
            lectureEvaluationId = evaluationReport.lectureEvaluation.id!!,
            userId = evaluationReport.userId,
            content = evaluationReport.content,
            isHidden = evaluationReport.isHidden,
        )
}
