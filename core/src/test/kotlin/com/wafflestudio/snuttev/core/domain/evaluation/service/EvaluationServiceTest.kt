package com.wafflestudio.snuttev.core.domain.evaluation.service

import com.wafflestudio.snuttev.core.common.error.EvaluationAlreadyExistsException
import com.wafflestudio.snuttev.core.common.error.NotMyLectureEvaluationException
import com.wafflestudio.snuttev.core.common.type.LectureClassification
import com.wafflestudio.snuttev.core.common.type.Semester
import com.wafflestudio.snuttev.core.domain.evaluation.dto.CreateEvaluationRequest
import com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.core.domain.evaluation.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.core.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.core.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.core.domain.lecture.repository.SemesterLectureRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random
import kotlin.random.nextInt

@SpringBootTest
@Transactional
class EvaluationServiceTest(
    @Autowired private val evaluationService: EvaluationService,
    @Autowired private val lectureEvaluationRepository: LectureEvaluationRepository,
    @Autowired private val lectureRepository: LectureRepository,
    @Autowired private val semesterLectureRepository: SemesterLectureRepository,
) {
    @BeforeEach
    fun setup() {
        val lecture = lectureRepository.save(
            Lecture(
                title = "소프트웨어 개발의 원리와 실습",
                instructor = "전병곤",
                department = "컴퓨터공학부",
                courseNumber = "M1522.002400",
                credit = 4,
                academicYear = "3학년",
                category = "",
                classification = LectureClassification.ELECTIVE_SUBJECT,
            )
        )
        for (year in 2001..2030) { // save 60 semesterLectures
            for (semester in listOf(Semester.SPRING.value, Semester.AUTUMN.value)) {
                semesterLectureRepository.save(
                    SemesterLecture(
                        lecture = lecture,
                        year = year,
                        semester = semester,
                        credit = 4,
                        academicYear = "3학년",
                        category = "",
                        classification = LectureClassification.ELECTIVE_SUBJECT,
                    )
                )
            }
        }
    }

    private val makeRandomScore = { Random.nextInt(1..5).toDouble() }

    data class RatingValues(
        var gradeSatisfaction: Double = 0.0,
        var teachingSkill: Double = 0.0,
        var gains: Double = 0.0,
        var lifeBalance: Double = 0.0,
        var rating: Double = 0.0,
    )

    @Test
    fun `test - createEvaluation`() {
        val userId = "1"
        val createEvaluationRequest = CreateEvaluationRequest(
            content = "좋은 강의였습니다.",
            gradeSatisfaction = 5.0,
            teachingSkill = 4.0,
            gains = 3.0,
            lifeBalance = 2.0,
            rating = 4.0,
        )
        val semesterLectureId = semesterLectureRepository.findAll().first().id!!

        val response = evaluationService.createEvaluation(
            userId = userId,
            semesterLectureId = semesterLectureId,
            createEvaluationRequest = createEvaluationRequest,
        )

        assertThat(response.userId).isEqualTo("1")
        assertThat(response.content).isEqualTo("좋은 강의였습니다.")
        assertThat(response.gradeSatisfaction).isEqualTo(5.0)
        assertThat(response.teachingSkill).isEqualTo(4.0)
        assertThat(response.gains).isEqualTo(3.0)
        assertThat(response.lifeBalance).isEqualTo(2.0)
        assertThat(response.rating).isEqualTo(4.0)
        assertThat(response.isHidden).isFalse
        assertThat(response.isReported).isFalse

        val lectureEvaluation = lectureEvaluationRepository.findByIdOrNull(response.id)
        assertThat(lectureEvaluation).isNotNull
        assertThat(lectureEvaluation!!.semesterLecture.id).isEqualTo(semesterLectureId)
        assertThat(lectureEvaluation.userId).isEqualTo("1")
    }

    @Test
    fun `test - createEvaluation - duplicated`() {
        val userId = "1"
        val createEvaluationRequest = CreateEvaluationRequest(
            content = "좋은 강의였습니다.",
            gradeSatisfaction = 5.0,
            teachingSkill = 4.0,
            gains = 3.0,
            lifeBalance = 2.0,
            rating = 4.0,
        )
        val semesterLectureId = semesterLectureRepository.findAll().first().id!!

        evaluationService.createEvaluation(
            userId = userId,
            semesterLectureId = semesterLectureId,
            createEvaluationRequest = createEvaluationRequest,
        )

        assertThatThrownBy {
            evaluationService.createEvaluation(
                userId = userId,
                semesterLectureId = semesterLectureId,
                createEvaluationRequest = createEvaluationRequest,
            )
        }.isInstanceOf(EvaluationAlreadyExistsException::class.java)
    }

    @Test
    fun `test - deleteEvaluation`() {
        val semesterLectureId = semesterLectureRepository.findAll().first().id!!
        val myUserId = "1"

        saveLectureEvaluation(userId = myUserId, semesterLectureId = semesterLectureId)

        val myEvaluationId = lectureEvaluationRepository.findAll().first { it.userId == myUserId }.id!!

        evaluationService.deleteEvaluation(userId = myUserId, lectureEvaluationId = myEvaluationId)

        val myEvaluation = lectureEvaluationRepository.findByIdOrNull(myEvaluationId)
        assertThat(myEvaluation).isNotNull // soft delete
        assertThat(myEvaluation!!.isHidden).isTrue
    }

    @Test
    fun `test - deleteEvaluation - notMyLectureEvaluation`() {
        val semesterLectureId = semesterLectureRepository.findAll().first().id!!
        val myUserId = "1"

        saveLectureEvaluation(userId = myUserId, semesterLectureId = semesterLectureId)
        saveLectureEvaluation(userId = "2", semesterLectureId = semesterLectureId)

        val otherEvaluationId = lectureEvaluationRepository.findAll().first { it.userId != myUserId }.id!!

        assertThatThrownBy {
            evaluationService.deleteEvaluation(userId = myUserId, lectureEvaluationId = otherEvaluationId)
        }.isInstanceOf(NotMyLectureEvaluationException::class.java)
    }

    @Test
    fun `test - deleteEvaluation - createEvaluation`() {
        val semesterLectureId = semesterLectureRepository.findAll().first().id!!
        val myUserId = "1"

        saveLectureEvaluation(userId = myUserId, semesterLectureId = semesterLectureId)

        val createEvaluationRequest = CreateEvaluationRequest(
            content = "좋은 강의였습니다.",
            gradeSatisfaction = 5.0,
            teachingSkill = 4.0,
            gains = 3.0,
            lifeBalance = 2.0,
            rating = 4.0,
        )

        assertThatThrownBy {
            evaluationService.createEvaluation(
                userId = myUserId,
                semesterLectureId = semesterLectureId,
                createEvaluationRequest = createEvaluationRequest,
            )
        }.isInstanceOf(EvaluationAlreadyExistsException::class.java)

        val myEvaluationId = lectureEvaluationRepository.findAll().first { it.userId == myUserId }.id!!
        evaluationService.deleteEvaluation(userId = myUserId, lectureEvaluationId = myEvaluationId)

        assertThatNoException().isThrownBy {
            evaluationService.createEvaluation(
                userId = myUserId,
                semesterLectureId = semesterLectureId,
                createEvaluationRequest = createEvaluationRequest,
            )
        }
    }

    @Test
    fun `test - getEvaluationSummaryOfLecture`() {
        val semesterLecture = semesterLectureRepository.findAll().first()

        val totalRatingValues = RatingValues()
        (1..5).map { it.toString() }.map { userId ->
            saveLectureEvaluation(userId, semesterLecture.id!!).let {
                totalRatingValues.gradeSatisfaction += it.gradeSatisfaction
                totalRatingValues.teachingSkill += it.teachingSkill
                totalRatingValues.gains += it.gains
                totalRatingValues.lifeBalance += it.lifeBalance
                totalRatingValues.rating += it.rating
            }
        }

        val response = evaluationService.getEvaluationSummaryOfLecture(semesterLecture.lecture.id!!)

        assertThat(response.id).isEqualTo(semesterLecture.lecture.id!!)
        assertThat(response.title).isEqualTo(semesterLecture.lecture.title)
        assertThat(response.instructor).isEqualTo(semesterLecture.lecture.instructor)
        assertThat(response.department).isEqualTo(semesterLecture.lecture.department)
        assertThat(response.courseNumber).isEqualTo(semesterLecture.lecture.courseNumber)
        assertThat(response.credit).isEqualTo(semesterLecture.lecture.credit)
        assertThat(response.academicYear).isEqualTo(semesterLecture.lecture.academicYear)
        assertThat(response.category).isEqualTo(semesterLecture.lecture.category)
        assertThat(response.classification).isEqualTo(semesterLecture.lecture.classification)
        assertThat(response.evaluation.avgGradeSatisfaction).isEqualTo(totalRatingValues.gradeSatisfaction / 5)
        assertThat(response.evaluation.avgTeachingSkill).isEqualTo(totalRatingValues.teachingSkill / 5)
        assertThat(response.evaluation.avgGains).isEqualTo(totalRatingValues.gains / 5)
        assertThat(response.evaluation.avgLifeBalance).isEqualTo(totalRatingValues.lifeBalance / 5)
        assertThat(response.evaluation.avgRating).isEqualTo(totalRatingValues.rating / 5)
    }

    @Test
    fun `test - getEvaluationsOfLecture - content`() {
        val semesterLecture = semesterLectureRepository.findAll().first()
        val myUserId = "1"

        (1..6).map { it.toString() }.map { userId ->
            saveLectureEvaluation(userId, semesterLecture.id!!)
        }

        val response = evaluationService.getEvaluationsOfLecture(myUserId, semesterLecture.lecture.id!!, cursor = null)

        assertThat(response.cursor).isNull()
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isTrue
        assertThat(response.totalCount).isEqualTo(6)
        assertThat(response.content).hasSize(5) // excluding my evaluations

        assertThat(response.content[0].userId).isNotEqualTo(myUserId)
        assertThat(response.content[0].content).isEqualTo("content")
        assertThat(response.content[0].year).isEqualTo(semesterLecture.year)
        assertThat(response.content[0].semester).isEqualTo(semesterLecture.semester)
        assertThat(response.content[0].lectureId).isEqualTo(semesterLecture.lecture.id)
        assertThat(response.content[0].isHidden).isFalse
        assertThat(response.content[0].isReported).isFalse
        assertThat(response.content[0].isModifiable).isFalse
        assertThat(response.content[0].isReportable).isTrue
    }

    @Test
    fun `test - getEvaluationsOfLecture - firstPage`() {
        val semesterLecture = semesterLectureRepository.findAll().first()
        val myUserId = "1"

        (1..30).map { it.toString() }.map { userId ->
            saveLectureEvaluation(userId, semesterLecture.id!!)
        }

        val lectureId = semesterLecture.lecture.id!!
        val response = evaluationService.getEvaluationsOfLecture(myUserId, lectureId, cursor = null)

        assertThat(response.cursor).isNotNull
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isFalse
        assertThat(response.totalCount).isEqualTo(30)
        assertThat(response.content).hasSize(20) // excluding my evaluations, but my evaluations are not included in the first page
    }

    @Test
    fun `test - getEvaluationsOfLecture - firstPageSize10`() {
        val semesterLecture = semesterLectureRepository.findAll().first()
        val myUserId = "1"

        (1..10).map { it.toString() }.map { userId ->
            saveLectureEvaluation(userId, semesterLecture.id!!)
        }

        val lectureId = semesterLecture.lecture.id!!
        val response = evaluationService.getEvaluationsOfLecture(myUserId, lectureId, cursor = null)

        assertThat(response.cursor).isNull()
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isTrue
        assertThat(response.totalCount).isEqualTo(10)
        assertThat(response.content).hasSize(9) // excluding my evaluations
    }

    @Test
    fun `test - getEvaluationsOfLecture - middlePage`() {
        val semesterLecture = semesterLectureRepository.findAll().first()
        val myUserId = "1"

        (1..50).map { it.toString() }.map { userId ->
            saveLectureEvaluation(userId, semesterLecture.id!!)
        }

        val lectureId = semesterLecture.lecture.id!!
        val firstPageResponse = evaluationService.getEvaluationsOfLecture(myUserId, lectureId, cursor = null)
        val response = evaluationService.getEvaluationsOfLecture(myUserId, lectureId, cursor = firstPageResponse.cursor)

        assertThat(response.cursor).isNotNull
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isFalse
        assertThat(response.totalCount).isEqualTo(50)
        assertThat(response.content).hasSize(20) // excluding my evaluations, but my evaluations are not included in the middle page
    }

    @Test
    fun `test - getEvaluationsOfLecture - lastPageSize20`() {
        val semesterLecture = semesterLectureRepository.findAll().first()
        val myUserId = "1"

        (1..61).map { it.toString() }.map { userId ->
            saveLectureEvaluation(userId, semesterLecture.id!!)
        }

        val lectureId = semesterLecture.lecture.id!!
        val firstPageResponse = evaluationService.getEvaluationsOfLecture(myUserId, lectureId, cursor = null)
        val middlePageResponse = evaluationService.getEvaluationsOfLecture(myUserId, lectureId, cursor = firstPageResponse.cursor)
        val response = evaluationService.getEvaluationsOfLecture(myUserId, lectureId, cursor = middlePageResponse.cursor)

        assertThat(response.cursor).isNull()
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isTrue
        assertThat(response.totalCount).isEqualTo(61) // including my evaluations
        assertThat(response.content).hasSize(20) // excluding my evaluations
    }

    @Test
    fun `test - getEvaluationsOfLecture - lastPageSize19`() {
        val semesterLecture = semesterLectureRepository.findAll().first()
        val myUserId = "1"

        (1..60).map { it.toString() }.map { userId ->
            saveLectureEvaluation(userId, semesterLecture.id!!)
        }

        val lectureId = semesterLecture.lecture.id!!
        val firstPageResponse = evaluationService.getEvaluationsOfLecture(myUserId, lectureId, cursor = null)
        val middlePageResponse = evaluationService.getEvaluationsOfLecture(myUserId, lectureId, cursor = firstPageResponse.cursor)
        val response = evaluationService.getEvaluationsOfLecture(myUserId, lectureId, cursor = middlePageResponse.cursor)

        assertThat(response.cursor).isNull()
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isTrue
        assertThat(response.totalCount).isEqualTo(60) // including my evaluations
        assertThat(response.content).hasSize(19) // excluding my evaluations
    }

    @Test
    fun `test - getEvaluationsOfLecture - ordering`() {
        val lecture = lectureRepository.findAll().first()
        val semesterLecture2010Spring = semesterLectureRepository.findByYearAndSemesterAndLecture(
            2010, Semester.SPRING.value, lecture,
        )!!
        val semesterLecture2010Autumn = semesterLectureRepository.findByYearAndSemesterAndLecture(
            2010, Semester.AUTUMN.value, lecture,
        )!!
        val semesterLecture2020Spring = semesterLectureRepository.findByYearAndSemesterAndLecture(
            2020, Semester.SPRING.value, lecture,
        )!!
        val semesterLecture2020Autumn = semesterLectureRepository.findByYearAndSemesterAndLecture(
            2020, Semester.AUTUMN.value, lecture,
        )!!

        val myUserId = "1"

        (1..60).map { it.toString() }.map { userId ->
            val semesterLecture = listOf(
                semesterLecture2010Spring, semesterLecture2010Autumn,
                semesterLecture2020Spring, semesterLecture2020Autumn,
            ).random()
            saveLectureEvaluation(userId, semesterLecture.id!!)
        }

        val response = evaluationService.getEvaluationsOfLecture(myUserId, lecture.id!!, cursor = null)

        for (i in 0..response.content.size - 2) {
            val current = response.content[i]
            val next = response.content[i + 1]
            assertThat(current.year).isGreaterThanOrEqualTo(next.year)
            if (current.year == next.year) {
                assertThat(current.semester).isGreaterThanOrEqualTo(next.semester)
                if (current.semester == next.semester) {
                    assertThat(current.id).isGreaterThan(next.id)
                }
            }
        }
    }

    @Test
    fun `test - getMyEvaluationsOfLecture`() {
        val lecture = lectureRepository.findAll().first()
        val semesterLecture2010Spring = semesterLectureRepository.findByYearAndSemesterAndLecture(
            2010, Semester.SPRING.value, lecture,
        )!!
        val semesterLecture2010Autumn = semesterLectureRepository.findByYearAndSemesterAndLecture(
            2010, Semester.AUTUMN.value, lecture,
        )!!
        val semesterLecture2020Spring = semesterLectureRepository.findByYearAndSemesterAndLecture(
            2020, Semester.SPRING.value, lecture,
        )!!
        val semesterLecture2020Autumn = semesterLectureRepository.findByYearAndSemesterAndLecture(
            2020, Semester.AUTUMN.value, lecture,
        )!!
        val myUserId = "1"

        (1..5).map { it.toString() }.map { userId ->
            saveLectureEvaluation(userId, semesterLecture2020Spring.id!!)
            saveLectureEvaluation(userId, semesterLecture2020Autumn.id!!)
            saveLectureEvaluation(userId, semesterLecture2010Spring.id!!)
            saveLectureEvaluation(userId, semesterLecture2010Autumn.id!!)
        }

        val response = evaluationService.getMyEvaluationsOfLecture(myUserId, lecture.id!!)

        assertThat(response.evaluations).hasSize(4)
        assertThat(response.evaluations[0].userId).isEqualTo(myUserId)
        assertThat(response.evaluations[0].content).isEqualTo("content")
        assertThat(response.evaluations[0].isHidden).isFalse
        assertThat(response.evaluations[0].isReported).isFalse
        assertThat(response.evaluations[0].isModifiable).isTrue
        assertThat(response.evaluations[0].isReportable).isFalse

        assertThat(response.evaluations[0].year).isEqualTo(2020)
        assertThat(response.evaluations[0].semester).isEqualTo(Semester.AUTUMN.value)
        assertThat(response.evaluations[0].lectureId).isEqualTo(lecture.id)
        assertThat(response.evaluations[1].year).isEqualTo(2020)
        assertThat(response.evaluations[1].semester).isEqualTo(Semester.SPRING.value)
        assertThat(response.evaluations[1].lectureId).isEqualTo(lecture.id)
        assertThat(response.evaluations[2].year).isEqualTo(2010)
        assertThat(response.evaluations[2].semester).isEqualTo(Semester.AUTUMN.value)
        assertThat(response.evaluations[2].lectureId).isEqualTo(lecture.id)
        assertThat(response.evaluations[3].year).isEqualTo(2010)
        assertThat(response.evaluations[3].semester).isEqualTo(Semester.SPRING.value)
        assertThat(response.evaluations[3].lectureId).isEqualTo(lecture.id)
    }

    @Test
    fun `test - getMyEvaluations - content`() {
        val userId = "1"
        saveLectureEvaluationsForMultipleLectures(userId, 5)

        val response = evaluationService.getMyEvaluations(userId = userId, cursor = null)

        assertThat(response.cursor).isNull()
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isTrue
        assertThat(response.totalCount).isEqualTo(5)
        assertThat(response.content).hasSize(5)

        assertThat(response.content[0].userId).isEqualTo(userId)
        assertThat(response.content[0].content).isEqualTo("content")
        assertThat(response.content[0].isHidden).isFalse
        assertThat(response.content[0].isReported).isFalse
        assertThat(response.content[0].isModifiable).isTrue
        assertThat(response.content[0].isReportable).isFalse
        assertThat(response.content[0].lecture.title).isEqualTo("소프트웨어 개발의 원리와 실습")
        assertThat(response.content[0].lecture.instructor).isEqualTo("전병곤")
    }

    @Test
    fun `test - getMyEvaluations - firstPage`() {
        val userId = "1"
        saveLectureEvaluationsForMultipleLectures(userId, 60)

        val response = evaluationService.getMyEvaluations(userId = userId, cursor = null)

        assertThat(response.cursor).isNotNull
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isFalse
        assertThat(response.totalCount).isEqualTo(60)
        assertThat(response.content).hasSize(20)
    }

    @Test
    fun `test - getMyEvaluations - firstPageSize10`() {
        val userId = "1"
        saveLectureEvaluationsForMultipleLectures(userId, 10)

        val response = evaluationService.getMyEvaluations(userId = userId, cursor = null)

        assertThat(response.cursor).isNull()
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isTrue
        assertThat(response.totalCount).isEqualTo(10)
        assertThat(response.content).hasSize(10)
    }

    @Test
    fun `test - getMyEvaluations - middlePage`() {
        val userId = "1"
        saveLectureEvaluationsForMultipleLectures(userId, 60)

        val firstPageResponse = evaluationService.getMyEvaluations(userId = userId, cursor = null)
        val response = evaluationService.getMyEvaluations(userId = userId, cursor = firstPageResponse.cursor)

        assertThat(response.cursor).isNotNull
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isFalse
        assertThat(response.totalCount).isEqualTo(60)
        assertThat(response.content).hasSize(20)
    }

    @Test
    fun `test - getMyEvaluations - lastPageSize20`() {
        val userId = "1"
        saveLectureEvaluationsForMultipleLectures(userId, 60)

        val firstPageResponse = evaluationService.getMyEvaluations(userId = userId, cursor = null)
        val middlePageResponse = evaluationService.getMyEvaluations(userId = userId, cursor = firstPageResponse.cursor)
        val response = evaluationService.getMyEvaluations(userId = userId, cursor = middlePageResponse.cursor)

        assertThat(response.cursor).isNull()
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isTrue
        assertThat(response.totalCount).isEqualTo(60)
        assertThat(response.content).hasSize(20)
    }

    @Test
    fun `test - getMyEvaluations - lastPageSize19`() {
        val userId = "1"
        saveLectureEvaluationsForMultipleLectures(userId, 59)

        val firstPageResponse = evaluationService.getMyEvaluations(userId = userId, cursor = null)
        val middlePageResponse = evaluationService.getMyEvaluations(userId = userId, cursor = firstPageResponse.cursor)
        val response = evaluationService.getMyEvaluations(userId = userId, cursor = middlePageResponse.cursor)

        assertThat(response.cursor).isNull()
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isTrue
        assertThat(response.totalCount).isEqualTo(59)
        assertThat(response.content).hasSize(19)
    }

    private fun saveLectureEvaluationsForMultipleLectures(userId: String, count: Int) {
        val semesterLectures = semesterLectureRepository.findAll()

        assertThat(semesterLectures).hasSizeGreaterThanOrEqualTo(count)

        semesterLectures.take(count).map {
            lectureEvaluationRepository.save(
                LectureEvaluation(
                    semesterLecture = it,
                    userId = userId,
                    content = "content",
                    gradeSatisfaction = makeRandomScore(),
                    teachingSkill = makeRandomScore(),
                    gains = makeRandomScore(),
                    lifeBalance = makeRandomScore(),
                    rating = makeRandomScore(),
                )
            )
        }
    }

    private fun saveLectureEvaluation(
        userId: String,
        semesterLectureId: Long,
    ): RatingValues {
        val ratingValues = RatingValues(
            gradeSatisfaction = makeRandomScore(),
            teachingSkill = makeRandomScore(),
            gains = makeRandomScore(),
            lifeBalance = makeRandomScore(),
            rating = makeRandomScore(),
        )

        lectureEvaluationRepository.save(
            LectureEvaluation(
                semesterLecture = semesterLectureRepository.findByIdOrNull(semesterLectureId)!!,
                userId = userId,
                content = "content",
                gradeSatisfaction = ratingValues.gradeSatisfaction,
                teachingSkill = ratingValues.teachingSkill,
                gains = ratingValues.gains,
                lifeBalance = ratingValues.lifeBalance,
                rating = ratingValues.rating,
            )
        )

        return ratingValues
    }
}
