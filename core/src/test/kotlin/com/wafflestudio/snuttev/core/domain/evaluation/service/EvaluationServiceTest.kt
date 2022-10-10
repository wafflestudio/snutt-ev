package com.wafflestudio.snuttev.core.domain.evaluation.service

import com.wafflestudio.snuttev.core.common.type.LectureClassification
import com.wafflestudio.snuttev.core.common.type.Semester
import com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.core.domain.evaluation.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.core.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.core.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.core.domain.lecture.repository.SemesterLectureRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

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

    @Test
    fun testGetMyEvaluationsContent() {
        val userId = "1"
        saveLectureEvaluations(userId, 5)

        val response = evaluationService.getMyEvaluations(userId = userId, cursor = null)

        assertThat(response.cursor).isNotNull
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isTrue
        assertThat(response.totalCount).isEqualTo(5)
        assertThat(response.content).hasSize(5)

        assertThat(response.content[0].userId).isEqualTo(userId)
        assertThat(response.content[0].content).isEqualTo("content")
        assertThat(response.content[0].gradeSatisfaction).isEqualTo(5.0)
        assertThat(response.content[0].teachingSkill).isEqualTo(5.0)
        assertThat(response.content[0].gains).isEqualTo(5.0)
        assertThat(response.content[0].lifeBalance).isEqualTo(5.0)
        assertThat(response.content[0].rating).isEqualTo(5.0)
        assertThat(response.content[0].isHidden).isFalse
        assertThat(response.content[0].isReported).isFalse
        assertThat(response.content[0].isModifiable).isTrue
        assertThat(response.content[0].isReportable).isFalse
        assertThat(response.content[0].lecture.title).isEqualTo("소프트웨어 개발의 원리와 실습")
        assertThat(response.content[0].lecture.instructor).isEqualTo("전병곤")
    }

    @Test
    fun testGetMyEvaluationsFirstPage() {
        val userId = "1"
        saveLectureEvaluations(userId, 60)

        val response = evaluationService.getMyEvaluations(userId = userId, cursor = null)

        assertThat(response.cursor).isNotNull
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isFalse
        assertThat(response.totalCount).isEqualTo(60)
        assertThat(response.content).hasSize(20)
    }

    @Test
    fun testGetMyEvaluationsFirstPageSize10() {
        val userId = "1"
        saveLectureEvaluations(userId, 10)

        val response = evaluationService.getMyEvaluations(userId = userId, cursor = null)

        assertThat(response.cursor).isNotNull
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isTrue
        assertThat(response.totalCount).isEqualTo(10)
        assertThat(response.content).hasSize(10)
    }

    @Test
    fun testGetMyEvaluationsMiddlePage() {
        val userId = "1"
        saveLectureEvaluations(userId, 60)

        val firstPageResponse = evaluationService.getMyEvaluations(userId = userId, cursor = null)
        val response = evaluationService.getMyEvaluations(userId = userId, cursor = firstPageResponse.cursor)

        assertThat(response.cursor).isNotNull
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isFalse
        assertThat(response.totalCount).isEqualTo(60)
        assertThat(response.content).hasSize(20)
    }

    @Test
    fun testGetMyEvaluationsLastPageSize20() {
        val userId = "1"
        saveLectureEvaluations(userId, 60)

        val firstPageResponse = evaluationService.getMyEvaluations(userId = userId, cursor = null)
        val middlePageResponse = evaluationService.getMyEvaluations(userId = userId, cursor = firstPageResponse.cursor)
        val response = evaluationService.getMyEvaluations(userId = userId, cursor = middlePageResponse.cursor)

        assertThat(response.cursor).isNotNull
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isTrue
        assertThat(response.totalCount).isEqualTo(60)
        assertThat(response.content).hasSize(20)
    }

    @Test
    fun testGetMyEvaluationsLastPageSize19() {
        val userId = "1"
        saveLectureEvaluations(userId, 59)

        val firstPageResponse = evaluationService.getMyEvaluations(userId = userId, cursor = null)
        val middlePageResponse = evaluationService.getMyEvaluations(userId = userId, cursor = firstPageResponse.cursor)
        val response = evaluationService.getMyEvaluations(userId = userId, cursor = middlePageResponse.cursor)

        assertThat(response.cursor).isNotNull
        assertThat(response.size).isEqualTo(20)
        assertThat(response.last).isTrue
        assertThat(response.totalCount).isEqualTo(59)
        assertThat(response.content).hasSize(19)
    }

    private fun saveLectureEvaluations(userId: String, count: Int) {
        val semesterLectures = semesterLectureRepository.findAll()

        assertThat(semesterLectures).hasSizeGreaterThanOrEqualTo(count)

        semesterLectures.take(count).map {
            lectureEvaluationRepository.save(
                LectureEvaluation(
                    semesterLecture = it,
                    userId = userId,
                    content = "content",
                    gradeSatisfaction = 5.0,
                    teachingSkill = 5.0,
                    gains = 5.0,
                    lifeBalance = 5.0,
                    rating = 5.0,
                )
            )
        }
    }
}
