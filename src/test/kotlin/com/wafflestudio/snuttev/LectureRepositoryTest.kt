package com.wafflestudio.snuttev

import com.wafflestudio.snuttev.dao.model.Lecture
import com.wafflestudio.snuttev.dao.model.LectureEvaluation
import com.wafflestudio.snuttev.dao.model.SemesterLecture
import com.wafflestudio.snuttev.dao.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.dao.repository.LectureRepository
import com.wafflestudio.snuttev.dao.repository.SemesterLectureRepository
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.TestConstructor
import javax.persistence.EntityManager

@DataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LectureRepositoryTest(
    val entityManager: EntityManager,
    val lectureRepository: LectureRepository,
    val semesterLectureRepository: SemesterLectureRepository,
    val lectureEvaluationRepository: LectureEvaluationRepository,
) {

    @Test
    fun `lectureId 로 lectureEvaluation 을 찾을 수 있다`() {
        // given
        val lecture1 = Lecture(
            title = "title1",
            instructor = "instructor",
            department = "department",
            courseNumber = "coursenumber1",
            credit = 3,
            academicYear = "1학년",
            category = "category",
            classfication = "classification",
        )
        lectureRepository.save(lecture1)

        val semesterLecture1 = SemesterLecture(
            lecture = lecture1,
            lectureNumber = "001",
            year = 2020,
            semester = 0,
            credit = 2,
            extraInfo = "",
            academicYear = "1학년",
            category = "category",
            classfication = "classification",
        )
        semesterLectureRepository.save(semesterLecture1)

        val lectureEvaluation1OfLecture1 = LectureEvaluation(
            semesterLecture = semesterLecture1,
            userId = "asdf1",
            content = "asdf1",
            likeCount = 0,
            dislikeCount = 0,
        )
        lectureEvaluationRepository.save(lectureEvaluation1OfLecture1)
        val lectureEvaluation2OfLecture1 = LectureEvaluation(
            semesterLecture = semesterLecture1,
            userId = "asdf2",
            content = "asdf2",
            likeCount = 0,
            dislikeCount = 0,
        )
        lectureEvaluationRepository.save(lectureEvaluation2OfLecture1)

        val lecture2 = Lecture(
            title = "title2",
            instructor = "instructor",
            department = "department",
            courseNumber = "coursenumber2",
            credit = 3,
            academicYear = "1학년",
            category = "category",
            classfication = "classification",
        )
        lectureRepository.save(lecture2)
        val semesterLecture2 = SemesterLecture(
            lecture = lecture2,
            lectureNumber = "002",
            year = 2020,
            semester = 0,
            credit = 2,
            extraInfo = "",
            academicYear = "1학년",
            category = "category",
            classfication = "classification",
        )
        semesterLectureRepository.save(semesterLecture2)
        val lectureEvaluation1OfLecture2 = LectureEvaluation(
            semesterLecture = semesterLecture2,
            userId = "asdf",
            content = "asdf",
            likeCount = 0,
            dislikeCount = 0,
        )
        lectureEvaluationRepository.save(lectureEvaluation1OfLecture2)

        entityManager.flush()
        entityManager.clear()

        // when
        val result = lectureRepository.findByIdOrNull(lecture1.id!!)!!.semesterLectures.flatMap {
            it.lectureEvaluations.flatMap { lectureEvaluation ->
                listOf(lectureEvaluation.id)
            }
        }

        // then
        then(result.size).isEqualTo(2)
        then(result).contains(lectureEvaluation1OfLecture1.id)
        then(result).contains(lectureEvaluation2OfLecture1.id)
        then(result).doesNotContain(lectureEvaluation1OfLecture2.id)
    }
}
