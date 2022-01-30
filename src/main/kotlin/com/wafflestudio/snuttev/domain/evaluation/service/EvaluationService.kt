package com.wafflestudio.snuttev.domain.evaluation.service

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.wafflestudio.snuttev.domain.common.dto.CursorPaginationResponse
import com.wafflestudio.snuttev.domain.evaluation.dto.*
import com.wafflestudio.snuttev.domain.evaluation.model.EvaluationReport
import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithLecture
import com.wafflestudio.snuttev.domain.evaluation.repository.EvaluationReportRepository
import com.wafflestudio.snuttev.domain.evaluation.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.domain.tag.repository.TagRepository
import com.wafflestudio.snuttev.error.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.lang.NumberFormatException
import java.math.BigInteger
import javax.transaction.Transactional

@Service
class EvaluationService(
    private val semesterLectureRepository: SemesterLectureRepository,
    private val lectureEvaluationRepository: LectureEvaluationRepository,
    private val lectureRepository: LectureRepository,
    private val tagRepository: TagRepository,
    private val evaluationReportRepository: EvaluationReportRepository,
) {
    @Autowired
    private lateinit var self: EvaluationService

    private val defaultPageSize = 3

    @CacheEvict("tag-recent-evaluations")
    fun createEvaluation(
        userId: String,
        semesterLectureId: Long,
        createEvaluationRequest: CreateEvaluationRequest
    ): LectureEvaluationDto {
        val semesterLecture = semesterLectureRepository.findByIdOrNull(semesterLectureId) ?: throw SemesterLectureNotFoundException

        if (lectureEvaluationRepository.existsBySemesterLectureIdAndUserIdAndIsHiddenFalse(semesterLectureId, userId)) {
            throw EvaluationAlreadyExistsException
        }

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
    ): CursorPaginationResponse<LectureEvaluationWithSemesterDto> {
        val pageable = PageRequest.of(0, defaultPageSize)
        val lectureEvaluationsCount = lectureEvaluationRepository.countByLectureId(lectureId)

        val lectureEvaluationsWithLecture = cursor?.let {
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

            lectureEvaluationRepository.findByLectureIdLessThanOrderByDesc(lectureId, userId, cursorYear, cursorSemester, cursorId, pageable).map {
                LectureEvaluationWithLecture(
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
                    lectureId = (it["lectureId"] as BigInteger).toLong(),
                    lectureTitle = it["lectureTitle"] as String?,
                    lectureInstructor = it["lectureInstructor"] as String?,
                )
            }
        } ?: lectureEvaluationRepository.findByLectureIdOrderByDesc(lectureId, userId, pageable)

        val lastLectureEvaluationWithLecture = lectureEvaluationsWithLecture.lastOrNull()

        val nextCursor = lastLectureEvaluationWithLecture?.let {
            "${it.year}-${it.semester}-${it.id}"
        }

        val isLast = lastLectureEvaluationWithLecture?.let {
            lectureEvaluationRepository.existsByLectureIdLessThan(lectureId, userId, it.year!!, it.semester!!, it.id!!) == null
        } ?: true

        return CursorPaginationResponse(
            content = lectureEvaluationsWithLecture.map { genLectureEvaluationWithSemesterDto(userId, it) },
            cursor = nextCursor,
            size = defaultPageSize,
            last = isLast,
            totalCount = lectureEvaluationsCount,
        )
    }

    fun getMyEvaluationsOfLecture(userId: String, lectureId: Long): LectureEvaluationsResponse {
        val lectureEvaluationsWithLecture = lectureEvaluationRepository.findByLectureIdAndUserIdOrderByDesc(lectureId, userId)
        return LectureEvaluationsResponse(
            evaluations = lectureEvaluationsWithLecture.map { genLectureEvaluationWithSemesterDto(userId, it) },
        )
    }

    fun getMainTagEvaluations(
        userId: String,
        tagId: Long,
        cursor: String?,
    ): CursorPaginationResponse<LectureEvaluationWithLectureDto> {
        val tag = tagRepository.findByIdOrNull(tagId) ?: throw TagNotFoundException

        val pageable = PageRequest.of(0, defaultPageSize)

        val cursorId: Long?
        try {
            cursorId = cursor?.toLong()
        } catch (e: NumberFormatException) {
            throw WrongCursorFormatException
        }

        val cursorPaginationForLectureEvaluationWithLectureDto = when (tag.name) {
            "최신" -> self.getLectureEvaluationsWithLectureFromTagRecent(cursorId, pageable)
            "추천" -> self.getLectureEvaluationsWithLectureFromTagRecommended(cursorId, pageable)
            "명강" -> self.getLectureEvaluationsWithLectureFromTagFine(cursorId, pageable)
            "꿀강" -> self.getLectureEvaluationsWithLectureFromTagHoney(cursorId, pageable)
            "고진감래" -> self.getLectureEvaluationsWithLectureFromTagPainsGains(cursorId, pageable)
            else -> throw WrongMainTagException
        }

        return CursorPaginationResponse(
            content = cursorPaginationForLectureEvaluationWithLectureDto.lectureEvaluationsWithLecture.map {
                genLectureEvaluationWithLectureDto(userId, it)
            },
            cursor = cursorPaginationForLectureEvaluationWithLectureDto.cursor,
            size = defaultPageSize,
            last = cursorPaginationForLectureEvaluationWithLectureDto.last!!,
        )
    }

    @CacheEvict("tag-recent-evaluations", "tag-recommended-evaluations", "tag-fine-evaluations", "tag-honey-evaluations", "tag-painsgains-evaluations", allEntries = true)
    @Transactional
    fun deleteLectureEvaluation(
        userId: String,
        lectureEvaluationId: Long,
    ) {
        val lectureEvaluation = lectureEvaluationRepository.findByIdAndIsHiddenFalse(lectureEvaluationId) ?: throw LectureEvaluationNotFoundException
        if (lectureEvaluation.userId != userId) {
            throw NotMyLectureEvaluationException
        }

        lectureEvaluation.isHidden = true
    }

    fun reportLectureEvaluation(
        userId: String,
        lectureEvaluationId: Long,
        createEvaluationReportRequest: CreateEvaluationReportRequest,
    ): EvaluationReportDto {
        val lectureEvaluation = lectureEvaluationRepository.findByIdAndIsHiddenFalse(lectureEvaluationId) ?: throw LectureEvaluationNotFoundException
        if (lectureEvaluation.userId == userId) {
            throw MyLectureEvaluationException
        }

        if (evaluationReportRepository.existsByLectureEvaluationIdAndUserId(lectureEvaluationId, userId)) {
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

    @Cacheable("tag-recent-evaluations")
    fun getLectureEvaluationsWithLectureFromTagRecent(cursorId: Long?, pageable: Pageable): CursorPaginationForLectureEvaluationWithLectureDto {
        val lectureEvaluationsWithLecture = cursorId?.let {
            lectureEvaluationRepository.findByLecturesRecentLessThanOrderByDesc(cursorId, pageable)
        } ?: lectureEvaluationRepository.findByLecturesRecentOrderByDesc(pageable)

        val lastLectureEvaluationWithLecture = lectureEvaluationsWithLecture.lastOrNull()

        val nextCursor = lastLectureEvaluationWithLecture?.id?.toString()

        val isLast = lastLectureEvaluationWithLecture?.let {
            lectureEvaluationRepository.existsByLecturesRecentLessThan(it.id!!) == null
        } ?: true

        return CursorPaginationForLectureEvaluationWithLectureDto(
            lectureEvaluationsWithLecture = lectureEvaluationsWithLecture,
            cursor = nextCursor,
            last = isLast,
        )
    }

    @Cacheable("tag-recommended-evaluations")
    fun getLectureEvaluationsWithLectureFromTagRecommended(cursorId: Long?, pageable: Pageable): CursorPaginationForLectureEvaluationWithLectureDto {
        val lectureEvaluationsWithLecture = cursorId?.let {
            lectureEvaluationRepository.findByLecturesRecommendedLessThanOrderByDesc(cursorId, pageable)
        } ?: lectureEvaluationRepository.findByLecturesRecommendedOrderByDesc(pageable)

        val lastLectureEvaluationWithLecture = lectureEvaluationsWithLecture.lastOrNull()

        val nextCursor = lastLectureEvaluationWithLecture?.id?.toString()

        val isLast = lastLectureEvaluationWithLecture?.let {
            lectureEvaluationRepository.existsByLecturesRecommendedLessThan(it.id!!) == null
        } ?: true

        return CursorPaginationForLectureEvaluationWithLectureDto(
            lectureEvaluationsWithLecture = lectureEvaluationsWithLecture,
            cursor = nextCursor,
            last = isLast,
        )
    }

    @Cacheable("tag-fine-evaluations")
    fun getLectureEvaluationsWithLectureFromTagFine(cursorId: Long?, pageable: Pageable): CursorPaginationForLectureEvaluationWithLectureDto {
        val lectureEvaluationsWithLecture = cursorId?.let {
            lectureEvaluationRepository.findByLecturesFineLessThanOrderByDesc(cursorId, pageable)
        } ?: lectureEvaluationRepository.findByLecturesFineOrderByDesc(pageable)

        val lastLectureEvaluationWithLecture = lectureEvaluationsWithLecture.lastOrNull()

        val nextCursor = lastLectureEvaluationWithLecture?.id?.toString()

        val isLast = lastLectureEvaluationWithLecture?.let {
            lectureEvaluationRepository.existsByLecturesFineLessThan(it.id!!) == null
        } ?: true

        return CursorPaginationForLectureEvaluationWithLectureDto(
            lectureEvaluationsWithLecture = lectureEvaluationsWithLecture,
            cursor = nextCursor,
            last = isLast,
        )
    }

    @Cacheable("tag-honey-evaluations")
    fun getLectureEvaluationsWithLectureFromTagHoney(cursorId: Long?, pageable: Pageable): CursorPaginationForLectureEvaluationWithLectureDto {
        val lectureEvaluationsWithLecture = cursorId?.let {
            lectureEvaluationRepository.findByLecturesHoneyLessThanOrderByDesc(cursorId, pageable)
        } ?: lectureEvaluationRepository.findByLecturesHoneyOrderByDesc(pageable)

        val lastLectureEvaluationWithLecture = lectureEvaluationsWithLecture.lastOrNull()

        val nextCursor = lastLectureEvaluationWithLecture?.id?.toString()

        val isLast = lastLectureEvaluationWithLecture?.let {
            lectureEvaluationRepository.existsByLecturesHoneyLessThan(it.id!!) == null
        } ?: true

        return CursorPaginationForLectureEvaluationWithLectureDto(
            lectureEvaluationsWithLecture = lectureEvaluationsWithLecture,
            cursor = nextCursor,
            last = isLast,
        )
    }

    @Cacheable("tag-painsgains-evaluations")
    fun getLectureEvaluationsWithLectureFromTagPainsGains(cursorId: Long?, pageable: Pageable): CursorPaginationForLectureEvaluationWithLectureDto {
        val lectureEvaluationsWithLecture = cursorId?.let {
            lectureEvaluationRepository.findByLecturesPainsGainsLessThanOrderByDesc(cursorId, pageable)
        } ?: lectureEvaluationRepository.findByLecturesPainsGainsOrderByDesc(pageable)

        val lastLectureEvaluationWithLecture = lectureEvaluationsWithLecture.lastOrNull()

        val nextCursor = lastLectureEvaluationWithLecture?.id?.toString()

        val isLast = lastLectureEvaluationWithLecture?.let {
            lectureEvaluationRepository.existsByLecturesPainsGainsLessThan(it.id!!) == null
        } ?: true

        return CursorPaginationForLectureEvaluationWithLectureDto(
            lectureEvaluationsWithLecture = lectureEvaluationsWithLecture,
            cursor = nextCursor,
            last = isLast,
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

    private fun genLectureEvaluationWithSemesterDto(
        userId: String,
        lectureEvaluationWithLecture: LectureEvaluationWithLecture,
    ): LectureEvaluationWithSemesterDto =
        LectureEvaluationWithSemesterDto(
            id = lectureEvaluationWithLecture.id!!,
            userId = lectureEvaluationWithLecture.userId!!,
            content = lectureEvaluationWithLecture.content!!,
            gradeSatisfaction = lectureEvaluationWithLecture.gradeSatisfaction!!,
            teachingSkill = lectureEvaluationWithLecture.teachingSkill!!,
            gains = lectureEvaluationWithLecture.gains!!,
            lifeBalance = lectureEvaluationWithLecture.lifeBalance!!,
            rating = lectureEvaluationWithLecture.rating!!,
            likeCount = lectureEvaluationWithLecture.likeCount!!,
            dislikeCount = lectureEvaluationWithLecture.dislikeCount!!,
            isHidden = lectureEvaluationWithLecture.isHidden!!,
            isReported = lectureEvaluationWithLecture.isReported!!,
            year = lectureEvaluationWithLecture.year!!,
            semester = lectureEvaluationWithLecture.semester!!,
            lectureId = lectureEvaluationWithLecture.lectureId!!,
            isModifiable = lectureEvaluationWithLecture.userId == userId,
            isReportable = lectureEvaluationWithLecture.userId != userId,
        )

    private fun genLectureEvaluationWithLectureDto(
        userId: String,
        lectureEvaluationWithLecture: LectureEvaluationWithLecture,
    ): LectureEvaluationWithLectureDto =
        LectureEvaluationWithLectureDto(
            id = lectureEvaluationWithLecture.id!!,
            userId = lectureEvaluationWithLecture.userId!!,
            content = lectureEvaluationWithLecture.content!!,
            gradeSatisfaction = lectureEvaluationWithLecture.gradeSatisfaction!!,
            teachingSkill = lectureEvaluationWithLecture.teachingSkill!!,
            gains = lectureEvaluationWithLecture.gains!!,
            lifeBalance = lectureEvaluationWithLecture.lifeBalance!!,
            rating = lectureEvaluationWithLecture.rating!!,
            likeCount = lectureEvaluationWithLecture.likeCount!!,
            dislikeCount = lectureEvaluationWithLecture.dislikeCount!!,
            isHidden = lectureEvaluationWithLecture.isHidden!!,
            isReported = lectureEvaluationWithLecture.isReported!!,
            year = lectureEvaluationWithLecture.year!!,
            semester = lectureEvaluationWithLecture.semester!!,
            lecture = SimpleLectureDto(
                id = lectureEvaluationWithLecture.lectureId!!,
                title = lectureEvaluationWithLecture.lectureTitle!!,
                instructor = lectureEvaluationWithLecture.lectureInstructor!!,
            ),
            isModifiable = lectureEvaluationWithLecture.userId == userId,
            isReportable = lectureEvaluationWithLecture.userId != userId,
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

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
data class CursorPaginationForLectureEvaluationWithLectureDto(
    val lectureEvaluationsWithLecture: List<LectureEvaluationWithLecture> = emptyList(),

    val cursor: String? = null,

    val last: Boolean? = null,
)
