package com.wafflestudio.snuttev

import com.wafflestudio.snuttev.dao.model.Lecture
import com.wafflestudio.snuttev.dao.model.LectureEvaluation
import com.wafflestudio.snuttev.dao.model.SemesterLecture
import com.wafflestudio.snuttev.dao.repository.LectureEvaluationRepository
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestConstructor
import org.junit.jupiter.api.Assertions.*

@DataJpaTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LectureEvaluationRepositoryTest(
    val lectureEvaluationRepository: LectureEvaluationRepository,
    val entityManager: TestEntityManager
) {

    @Test
    fun `lectureId 로 lectureEvaluation 을 찾을 수 있다`() {
        // given
        val lecture1 =
            entityManager.persist(Lecture("title", "instructor", "department", "coursenumber", "lecturenumber"))!!
        val semesterLecture1 = entityManager.persist(SemesterLecture(lecture1, 2020, 0, 2, ""))!!
        val lectureEvaluation1OfLecture1 =
            entityManager.persist(LectureEvaluation(semesterLecture1, "asdf1", "asdf1", 0, 0))!!
        val lectureEvaluation2OfLecture1 =
            entityManager.persist(LectureEvaluation(semesterLecture1, "asdf2", "asdf2", 0, 0))!!

        val lecture2 =
            entityManager.persist(Lecture("title", "instructor", "department", "coursenumber", "lecturenumber"))!!
        val semesterLecture2 = entityManager.persist(SemesterLecture(lecture2, 2020, 0, 2, ""))!!
        val lectureEvaluation2OfLecture2 =
            entityManager.persist(LectureEvaluation(semesterLecture2, "asdf", "asdf", 0, 0))!!

        // when
        val result = lectureEvaluationRepository.findByLectureId(lectureId = lecture1.id!!)

        // then
        assertEquals(result.size, 2)
        assert(result.contains(lectureEvaluation1OfLecture1))
        assert(result.contains(lectureEvaluation2OfLecture1))
        assert(result.contains(lectureEvaluation2OfLecture2).not())
    }
}
