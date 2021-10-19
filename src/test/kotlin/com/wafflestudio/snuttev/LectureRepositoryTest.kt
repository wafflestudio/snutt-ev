package com.wafflestudio.snuttev

import com.wafflestudio.snuttev.dao.model.Lecture
import com.wafflestudio.snuttev.dao.model.LectureEvaluation
import com.wafflestudio.snuttev.dao.model.SemesterLecture
import com.wafflestudio.snuttev.dao.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.dao.repository.LectureRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.TestConstructor
import org.junit.jupiter.api.Assertions.*
import org.assertj.core.api.BDDAssertions.then

@DataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LectureRepositoryTest(
    val lectureRepository: LectureRepository
) {

    @Test
    fun `lectureId 로 lectureEvaluation 을 찾을 수 있다`() {
        // given
        val lecture1 = Lecture("title", "instructor", "department", "coursenumber", "lecturenumber")
        val semesterLecture1 = SemesterLecture(lecture1, 2020, 0, 2, "")
        val lectureEvaluation1OfLecture1 = LectureEvaluation(semesterLecture1, "asdf1", "asdf1", 0, 0)
        val lectureEvaluation2OfLecture1 = LectureEvaluation(semesterLecture1, "asdf2", "asdf2", 0, 0)

        semesterLecture1.lectureEvaluations = listOf(lectureEvaluation1OfLecture1, lectureEvaluation2OfLecture1)
        lecture1.semesterLectures = listOf(semesterLecture1)

        val lecture2 = Lecture("title", "instructor", "department", "coursenumber", "lecturenumber")
        val semesterLecture2 = SemesterLecture(lecture2, 2020, 0, 2, "")
        val lectureEvaluation1OfLecture2 = LectureEvaluation(semesterLecture2, "asdf", "asdf", 0, 0)

        semesterLecture2.lectureEvaluations = listOf(lectureEvaluation1OfLecture2)
        lecture2.semesterLectures = listOf(semesterLecture2)

        lectureRepository.save(lecture1)
        lectureRepository.save(lecture2)

        // when
        val result = lectureRepository.getById(lecture1.id!!).semesterLectures.flatMap { it.lectureEvaluations }

        // then
        then(result.size).isEqualTo(2)
        then(result).contains(lectureEvaluation1OfLecture1)
        then(result).contains(lectureEvaluation2OfLecture1)
        then(result).doesNotContain(lectureEvaluation1OfLecture2)
    }
}
