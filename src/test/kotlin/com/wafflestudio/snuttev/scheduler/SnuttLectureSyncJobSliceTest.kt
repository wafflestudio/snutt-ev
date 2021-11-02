package com.wafflestudio.snuttev.scheduler

import com.wafflestudio.snuttev.common.Semester
import com.wafflestudio.snuttev.dao.repository.LectureRepository
import com.wafflestudio.snuttev.dao.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.scheduler.lecture_crawler.SemesterUtils
import com.wafflestudio.snuttev.scheduler.lecture_crawler.SnuttLectureSyncJobContext
import com.wafflestudio.snuttev.scheduler.lecture_crawler.model.SnuttSemesterLecture
import com.wafflestudio.snuttev.scheduler.lecture_crawler.model.SnuttTimePlace
import com.wafflestudio.snuttev.scheduler.lecture_crawler.repository.SnuttSemesterLectureRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.TestConstructor
import javax.transaction.Transactional


@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
internal class SnuttLectureSyncJobSliceTest(
    private val snuttLectureSyncJobContext: SnuttLectureSyncJobContext,
    private val lectureRepository: LectureRepository,
    private val semesterLectureRepository: SemesterLectureRepository,
) {
    @MockBean(name = "snuttSemesterLectureRepository")
    lateinit var snuttSemesterLectureRepository: SnuttSemesterLectureRepository

    @MockBean(name = "semesterUtils")
    lateinit var semesterUtils: SemesterUtils

    private fun makeTestSnuttSemesterLecture(
        year: Int,
        semester: Int,
        courseNumber: String,
        lectureNumber: String,
        instructor: String,
        category: String
    ): SnuttSemesterLecture {
        return SnuttSemesterLecture(
            id = "",
            classification = "",
            department = "",
            academic_year = "",
            courseTitle = "",
            credit = 0,
            classTime = "",
            classTimeJson = listOf(SnuttTimePlace(1, 2, 3, "")),
            classTimeMask = listOf(3),
            instructor = instructor,
            quota = 0,
            remark = "",
            category = category,
            year = year,
            semester = semester,
            courseNumber = courseNumber,
            lectureNumber = lectureNumber
        )
    }

    val autumnSemesterLectures = listOf<SnuttSemesterLecture>(
        makeTestSnuttSemesterLecture(2021, 3, "1", "1", "1", "a"),
        makeTestSnuttSemesterLecture(2021, 3, "1", "2", "1", "b"),
        makeTestSnuttSemesterLecture(2021, 3, "2", "1", "2", "c"),
        makeTestSnuttSemesterLecture(2021, 3, "2", "2", "1", "d"),
        makeTestSnuttSemesterLecture(2021, 3, "3", "1", "3", "e"),
        makeTestSnuttSemesterLecture(2021, 3, "3", "2", "4", "f"),
    )

    val winterSemesterLectures = listOf<SnuttSemesterLecture>(
        makeTestSnuttSemesterLecture(2021, 4, "1", "1", "1", "b"),
        makeTestSnuttSemesterLecture(2021, 4, "1", "2", "3", "c"),
        makeTestSnuttSemesterLecture(2021, 4, "2", "1", "3", "d"),
        makeTestSnuttSemesterLecture(2021, 4, "2", "2", "4", "e"),
        makeTestSnuttSemesterLecture(2021, 4, "5", "1", "5", "f"),
        makeTestSnuttSemesterLecture(2021, 4, "5", "1", "2", "g"),
        makeTestSnuttSemesterLecture(2021, 4, "6", "1", "6", "i"),
    )

    @Test
    @Transactional
    fun `snutt 전체 강의 데이터 migration 강의 중복 제거 테스트`() {
        given(snuttSemesterLectureRepository.findAll()).willReturn(autumnSemesterLectures + winterSemesterLectures)

        snuttLectureSyncJobContext.migrateAllLectureDataFromSnutt()
        val lectures = lectureRepository.findAll()

        assertEquals(lectures.size, 11)
    }

    @Test
    @Transactional
    fun `snutt 전체 강의 데이터 migration semesterLecture 생성 테스트`() {
        given(snuttSemesterLectureRepository.findAll()).willReturn(autumnSemesterLectures + winterSemesterLectures)

        snuttLectureSyncJobContext.migrateAllLectureDataFromSnutt()
        val semesterLectures = semesterLectureRepository.findAll()

        assertEquals(semesterLectures.size, 13)
    }

    @Test
    @Transactional
    fun `snutt 최근 수강편람 강의 데이터 migration 테스트`() {
        given(semesterUtils.getCurrentYearAndSemester()).willReturn(2021 to Semester.SUMMER)
        given(semesterUtils.getYearAndSemesterOfNextSemester()).willReturn(2021 to Semester.AUTUMN)
        given(snuttSemesterLectureRepository.existsByYearAndSemester(2021, 3)).willReturn(true)
        given(snuttSemesterLectureRepository.findMongoSemesterLecturesByYearAndSemester(2021, 3)).willReturn(
            autumnSemesterLectures
        )

        snuttLectureSyncJobContext.migrateLatestSemesterLectureDataFromSnutt()
        val lectures = lectureRepository.findAll()
        val semesterLectures = semesterLectureRepository.findAll()

        assertEquals(lectures.size, 5)
        assertEquals(semesterLectures.size, 6)
    }

    @Test
    @Transactional
    fun `snutt데이터 그대로일 때 sync 여러번 일어나도 기존 데이터 유지`() {
        given(snuttSemesterLectureRepository.findAll()).willReturn(autumnSemesterLectures + winterSemesterLectures)
        snuttLectureSyncJobContext.migrateAllLectureDataFromSnutt()

        val semesterLecturesBefore = semesterLectureRepository.findAll()
        snuttLectureSyncJobContext.migrateAllLectureDataFromSnutt()
        val semesterLecturesAfter = semesterLectureRepository.findAll()

        assertIterableEquals(semesterLecturesAfter, semesterLecturesBefore)
    }

    @Test
    @Transactional
    fun `업데이트 된 snutt 데이터 sync 할 때 기존 semesterLecture 업데이트`() {

        given(snuttSemesterLectureRepository.findAll()).willReturn(
            listOf(
                makeTestSnuttSemesterLecture(2021, 3, "1", "1", "1", ""),
            )
        )
        given(semesterUtils.getCurrentYearAndSemester()).willReturn(2021 to Semester.SUMMER)
        given(semesterUtils.getYearAndSemesterOfNextSemester()).willReturn(2021 to Semester.AUTUMN)
        given(snuttSemesterLectureRepository.existsByYearAndSemester(2021, 3)).willReturn(true)
        given(snuttSemesterLectureRepository.findMongoSemesterLecturesByYearAndSemester(2021, 3)).willReturn(
            autumnSemesterLectures
        )

        snuttLectureSyncJobContext.migrateAllLectureDataFromSnutt()
        snuttLectureSyncJobContext.migrateLatestSemesterLectureDataFromSnutt()

        val targetSemesterLectures = semesterLectureRepository.findAll().first()

        assertEquals(targetSemesterLectures.category, "a")
    }

    @Test
    fun `최신 학기 정보로 lecture 정보 최신화`() {
        given(snuttSemesterLectureRepository.findAll()).willReturn(autumnSemesterLectures)
        given(semesterUtils.getCurrentYearAndSemester()).willReturn(2021 to Semester.AUTUMN)
        given(semesterUtils.getYearAndSemesterOfNextSemester()).willReturn(2021 to Semester.WINTER)
        given(snuttSemesterLectureRepository.existsByYearAndSemester(2021, 4)).willReturn(true)
        given(snuttSemesterLectureRepository.findMongoSemesterLecturesByYearAndSemester(2021, 4)).willReturn(
            winterSemesterLectures
        )

        snuttLectureSyncJobContext.migrateAllLectureDataFromSnutt()

        val lectureBefore = lectureRepository.findByCourseNumberAndInstructorAndTitleAndDepartment("1", "1", "", "")

        snuttLectureSyncJobContext.migrateLatestSemesterLectureDataFromSnutt()

        val lectureAfter = lectureRepository.findByCourseNumberAndInstructorAndTitleAndDepartment("1", "1", "", "")

        assertNotNull(lectureBefore)
        assertNotNull(lectureAfter)
        assertEquals(lectureAfter!!.category, "b")
    }
}
