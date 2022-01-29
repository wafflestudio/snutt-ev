package com.wafflestudio.snuttev.domain.evaluation.service

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.wafflestudio.snuttev.domain.common.dto.CursorPaginationResponse
import com.wafflestudio.snuttev.domain.evaluation.dto.*
import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluationWithSemester
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
) {
    @Autowired
    private lateinit var self: EvaluationService

    private val defaultPageSize = 3

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
            summary = LectureEvaluationSummary(
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
                    lectureId = it["lectureId"] as Long,
                )
            }
        } ?: lectureEvaluationRepository.findByLectureIdOrderByDesc(lectureId, pageable)

        val lastLectureEvaluationWithSemester = lectureEvaluationsWithSemester.lastOrNull()

        val nextCursor = lastLectureEvaluationWithSemester?.let {
            "${it.year}-${it.semester}-${it.id}"
        }

        val isLast = lastLectureEvaluationWithSemester?.let {
            lectureEvaluationRepository.existsByLectureIdLessThan(lectureId, it.year!!, it.semester!!, it.id!!) == null
        } ?: true

        return CursorPaginationResponse(
            content = lectureEvaluationsWithSemester.map { genLectureEvaluationWithSemesterDto(userId, it) },
            cursor = nextCursor,
            size = defaultPageSize,
            last = isLast,
            totalCount = lectureEvaluationsCount,
        )
    }

    fun getMainTagEvaluations(
        userId: String,
        tagId: Long,
        cursor: String?,
    ): CursorPaginationResponse<LectureEvaluationWithSemesterDto> {
        val tag = tagRepository.findByIdOrNull(tagId) ?: throw TagNotFoundException

        val pageable = PageRequest.of(0, defaultPageSize)

        val cursorId: Long?
        try {
            cursorId = cursor?.toLong()
        } catch (e: NumberFormatException) {
            throw WrongCursorFormatException
        }

        val cursorPaginationForLectureEvaluationWithSemesterDto = when (tag.name) {
            "추천" -> self.getLectureEvaluationsWithSemesterFromTagRecommended(cursorId, pageable)
            "명강" -> self.getLectureEvaluationsWithSemesterFromTagFine(cursorId, pageable)
            "꿀강" -> self.getLectureEvaluationsWithSemesterFromTagHoney(cursorId, pageable)
            "고진감래" -> self.getLectureEvaluationsWithSemesterFromTagPainsGains(cursorId, pageable)
            else -> throw WrongMainTagException
        }

        return CursorPaginationResponse(
            content = cursorPaginationForLectureEvaluationWithSemesterDto.lectureEvaluationsWithSemester.map {
                genLectureEvaluationWithSemesterDto(userId, it)
            },
            cursor = cursorPaginationForLectureEvaluationWithSemesterDto.cursor,
            size = defaultPageSize,
            last = cursorPaginationForLectureEvaluationWithSemesterDto.last!!,
        )
    }

    @CacheEvict("tag-recommended-evaluations", "tag-fine-evaluations", "tag-honey-evaluations", "tag-painsgains-evaluations", allEntries = true)
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

    @Cacheable("tag-recommended-evaluations")
    fun getLectureEvaluationsWithSemesterFromTagRecommended(cursorId: Long?, pageable: Pageable): CursorPaginationForLectureEvaluationWithSemesterDto {
        val lectureEvaluationsWithSemester = cursorId?.let {
            lectureEvaluationRepository.findByLecturesRecommendedLessThanOrderByDesc(cursorId, pageable)
        } ?: lectureEvaluationRepository.findByLecturesRecommendedOrderByDesc(pageable)

        val lastLectureEvaluationWithSemester = lectureEvaluationsWithSemester.lastOrNull()

        val nextCursor = lastLectureEvaluationWithSemester?.id?.toString()

        val isLast = lastLectureEvaluationWithSemester?.let {
            lectureEvaluationRepository.existsByLecturesRecommendedLessThan(it.id!!) == null
        } ?: true

        return CursorPaginationForLectureEvaluationWithSemesterDto(
            lectureEvaluationsWithSemester = lectureEvaluationsWithSemester,
            cursor = nextCursor,
            last = isLast,
        )
    }

    @Cacheable("tag-fine-evaluations")
    fun getLectureEvaluationsWithSemesterFromTagFine(cursorId: Long?, pageable: Pageable): CursorPaginationForLectureEvaluationWithSemesterDto {
        val lectureEvaluationsWithSemester = cursorId?.let {
            lectureEvaluationRepository.findByLecturesFineLessThanOrderByDesc(cursorId, pageable)
        } ?: lectureEvaluationRepository.findByLecturesFineOrderByDesc(pageable)

        val lastLectureEvaluationWithSemester = lectureEvaluationsWithSemester.lastOrNull()

        val nextCursor = lastLectureEvaluationWithSemester?.id?.toString()

        val isLast = lastLectureEvaluationWithSemester?.let {
            lectureEvaluationRepository.existsByLecturesFineLessThan(it.id!!) == null
        } ?: true

        return CursorPaginationForLectureEvaluationWithSemesterDto(
            lectureEvaluationsWithSemester = lectureEvaluationsWithSemester,
            cursor = nextCursor,
            last = isLast,
        )
    }

    @Cacheable("tag-honey-evaluations")
    fun getLectureEvaluationsWithSemesterFromTagHoney(cursorId: Long?, pageable: Pageable): CursorPaginationForLectureEvaluationWithSemesterDto {
        val lectureEvaluationsWithSemester = cursorId?.let {
            lectureEvaluationRepository.findByLecturesHoneyLessThanOrderByDesc(cursorId, pageable)
        } ?: lectureEvaluationRepository.findByLecturesHoneyOrderByDesc(pageable)

        val lastLectureEvaluationWithSemester = lectureEvaluationsWithSemester.lastOrNull()

        val nextCursor = lastLectureEvaluationWithSemester?.id?.toString()

        val isLast = lastLectureEvaluationWithSemester?.let {
            lectureEvaluationRepository.existsByLecturesHoneyLessThan(it.id!!) == null
        } ?: true

        return CursorPaginationForLectureEvaluationWithSemesterDto(
            lectureEvaluationsWithSemester = lectureEvaluationsWithSemester,
            cursor = nextCursor,
            last = isLast,
        )
    }

    @Cacheable("tag-painsgains-evaluations")
    fun getLectureEvaluationsWithSemesterFromTagPainsGains(cursorId: Long?, pageable: Pageable): CursorPaginationForLectureEvaluationWithSemesterDto {
        val lectureEvaluationsWithSemester = cursorId?.let {
            lectureEvaluationRepository.findByLecturesPainsGainsLessThanOrderByDesc(cursorId, pageable)
        } ?: lectureEvaluationRepository.findByLecturesPainsGainsOrderByDesc(pageable)

        val lastLectureEvaluationWithSemester = lectureEvaluationsWithSemester.lastOrNull()

        val nextCursor = lastLectureEvaluationWithSemester?.id?.toString()

        val isLast = lastLectureEvaluationWithSemester?.let {
            lectureEvaluationRepository.existsByLecturesPainsGainsLessThan(it.id!!) == null
        } ?: true

        return CursorPaginationForLectureEvaluationWithSemesterDto(
            lectureEvaluationsWithSemester = lectureEvaluationsWithSemester,
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
        lectureEvaluationWithSemester: LectureEvaluationWithSemester,
    ): LectureEvaluationWithSemesterDto =
        LectureEvaluationWithSemesterDto(
            id = lectureEvaluationWithSemester.id!!,
            userId = lectureEvaluationWithSemester.userId!!,
            content = lectureEvaluationWithSemester.content!!,
            gradeSatisfaction = lectureEvaluationWithSemester.gradeSatisfaction!!,
            teachingSkill = lectureEvaluationWithSemester.teachingSkill!!,
            gains = lectureEvaluationWithSemester.gains!!,
            lifeBalance = lectureEvaluationWithSemester.lifeBalance!!,
            rating = lectureEvaluationWithSemester.rating!!,
            likeCount = lectureEvaluationWithSemester.likeCount!!,
            dislikeCount = lectureEvaluationWithSemester.dislikeCount!!,
            isHidden = lectureEvaluationWithSemester.isHidden!!,
            isReported = lectureEvaluationWithSemester.isReported!!,
            year = lectureEvaluationWithSemester.year!!,
            semester = lectureEvaluationWithSemester.semester!!,
            lectureId = lectureEvaluationWithSemester.lectureId!!,
            isModifiable = lectureEvaluationWithSemester.userId == userId,
            isReportable = lectureEvaluationWithSemester.userId != userId,
        )
}

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
data class CursorPaginationForLectureEvaluationWithSemesterDto(
    val lectureEvaluationsWithSemester: List<LectureEvaluationWithSemester> = emptyList(),

    val cursor: String? = null,

    val last: Boolean? = null,
)
