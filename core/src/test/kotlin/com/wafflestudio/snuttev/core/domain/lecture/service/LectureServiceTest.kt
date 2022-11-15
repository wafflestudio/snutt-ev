package com.wafflestudio.snuttev.core.domain.lecture.service

import com.wafflestudio.snuttev.core.common.type.LectureClassification
import com.wafflestudio.snuttev.core.common.type.Semester
import com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.core.domain.evaluation.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.core.domain.lecture.dto.SnuttLectureInfo
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
class LectureServiceTest(
    @Autowired private val lectureService: LectureService,
    @Autowired private val evaluationRepository: LectureEvaluationRepository,
    @Autowired private val lectureRepository: LectureRepository,
    @Autowired private val semesterLectureRepository: SemesterLectureRepository,
    @Autowired private val lectureEvaluationRepository: LectureEvaluationRepository,
) {

    @BeforeEach
    fun setUp() {
        saveLectures()
    }

    @Test
    fun `test - getSnuttevLecturesWithSnuttLectureInfos - 아무 리뷰도 작성하지 않았을 때`() {
        val userId = "user1"
        val semesterLectures = semesterLectureRepository.findAll().filter { it.semester == Semester.SPRING.value }
        val snuttLectureInfo = genSnuttLectureInfosFromSemesterLectures(semesterLectures)

        val response = lectureService.getSnuttevLecturesWithSnuttLectureInfos(
            userId = userId,
            snuttLectureInfos = snuttLectureInfo,
            excludeLecturesWithEvaluations = false,
        )

        assertThat(response.size).isEqualTo(semesterLectures.size)
    }

    @Test
    fun `test - getSnuttevLecturesWithSnuttLectureInfos - 작성한 리뷰가 있을 때`() {
        val userId = "user1"
        val semesterLectures = semesterLectureRepository.findAll().filter { it.semester == Semester.SPRING.value }
        val snuttLectureInfo = genSnuttLectureInfosFromSemesterLectures(semesterLectures)

        for (lecture in semesterLectures) {
            saveEvaluation(lecture, userId)
        }

        val response = lectureService.getSnuttevLecturesWithSnuttLectureInfos(
            userId = userId,
            snuttLectureInfos = snuttLectureInfo,
            excludeLecturesWithEvaluations = false,
        )

        assertThat(response.size).isEqualTo(semesterLectures.size)
    }

    @Test
    fun `test - getSnuttevLecturesWithSnuttLectureInfos - no-my-evaluations - 아무 리뷰도 작성하지 않았을 때`() {
        val userId = "user1"
        val semesterLectures = semesterLectureRepository.findAll().filter { it.semester == Semester.SPRING.value }
        val snuttLectureInfo = genSnuttLectureInfosFromSemesterLectures(semesterLectures)

        val response = lectureService.getSnuttevLecturesWithSnuttLectureInfos(
            userId = userId,
            snuttLectureInfos = snuttLectureInfo,
            excludeLecturesWithEvaluations = true,
        )

        assertThat(response.size).isEqualTo(semesterLectures.size)
    }

    @Test
    fun `test - getSnuttevLecturesWithSnuttLectureInfos - no-my-evaluations - 일부 작성했을 때`() {
        val userId = "user1"
        val semesterLectures = semesterLectureRepository.findAll().filter { it.semester == Semester.SPRING.value }
        val snuttLectureInfo = genSnuttLectureInfosFromSemesterLectures(semesterLectures)

        val userIds = listOf(userId, "user2")
        saveEvaluationsFromMultipleUsers(semesterLecture = semesterLectures.first(), userIds)

        val response = lectureService.getSnuttevLecturesWithSnuttLectureInfos(
            userId = userId,
            snuttLectureInfos = snuttLectureInfo,
            excludeLecturesWithEvaluations = true,
        )

        assertThat(response.size).isEqualTo(1)
    }

    @Test
    fun `test - getSnuttevLecturesWithSnuttLectureInfos - no-my-evaluations - 전부 작성했을 때`() {
        val userId = "user1"
        val semesterLectures = semesterLectureRepository.findAll().filter { it.semester == Semester.SPRING.value }
        val snuttLectureInfo = genSnuttLectureInfosFromSemesterLectures(semesterLectures)

        val userIds = listOf(userId, "user2")
        saveEvaluationsForMultipleLecturesFromMultipleUsers(semesterLectures, userIds)

        val response = lectureService.getSnuttevLecturesWithSnuttLectureInfos(
            userId = userId,
            snuttLectureInfos = snuttLectureInfo,
            excludeLecturesWithEvaluations = true,
        )

        assertThat(response.size).isEqualTo(0)
    }

    private fun saveLectures() {
        val lectures = lectureRepository.saveAll(
            listOf(
                Lecture(
                    title = "소프트웨어 개발의 원리와 실습",
                    instructor = "전병곤",
                    department = "컴퓨터공학부",
                    courseNumber = "M1522.002400",
                    credit = 4,
                    academicYear = "3학년",
                    category = "",
                    classification = LectureClassification.ELECTIVE_SUBJECT,
                ),
                Lecture(
                    title = "소프트웨어 개발의 원리와 실습",
                    instructor = "허충길",
                    department = "컴퓨터공학부",
                    courseNumber = "M1522.002401",
                    credit = 4,
                    academicYear = "3학년",
                    category = "",
                    classification = LectureClassification.ELECTIVE_SUBJECT,
                ),
            )
        )

        for (lecture in lectures) {
            for (semester in listOf(Semester.SPRING.value, Semester.AUTUMN.value)) {
                semesterLectureRepository.save(
                    SemesterLecture(
                        lecture = lecture,
                        year = 2022,
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

    private fun genSnuttLectureInfosFromSemesterLectures(semesterLectures: List<SemesterLecture>): List<SnuttLectureInfo> {
        val lectureInfos = semesterLectures
            .map {
                SnuttLectureInfo(
                    year = it.year,
                    semester = it.semester,
                    instructor = it.lecture.instructor,
                    courseNumber = it.lecture.courseNumber,
                )
            }
        return lectureInfos
    }

    private fun saveEvaluationsForMultipleLecturesFromMultipleUsers(semesterLectures: List<SemesterLecture>, userIds: List<String>) {
        for (lecture in semesterLectures) {
            saveEvaluationsFromMultipleUsers(lecture, userIds)
        }
    }

    private fun saveEvaluationsFromMultipleUsers(semesterLecture: SemesterLecture, userIds: List<String>) {
        for (userId in userIds) {
            saveEvaluation(semesterLecture, userId)
        }
    }

    private fun saveEvaluation(semesterLecture: SemesterLecture, userId: String) {
        evaluationRepository.save(
            LectureEvaluation(
                semesterLecture = semesterLecture,
                userId = userId,
                content = "",
                gradeSatisfaction = 5.0,
                teachingSkill = 5.0,
                gains = 5.0,
                lifeBalance = 5.0,
                rating = 5.0
            )
        )
    }
}
