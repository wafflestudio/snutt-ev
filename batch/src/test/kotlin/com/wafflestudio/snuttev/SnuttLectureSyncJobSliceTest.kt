package com.wafflestudio.snuttev

import com.wafflestudio.snuttev.common.type.Semester
import com.wafflestudio.snuttev.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.sync.SemesterUtils
import com.wafflestudio.snuttev.sync.model.SnuttSemesterLecture
import com.wafflestudio.snuttev.sync.model.SnuttTimePlace
import com.wafflestudio.snuttev.sync.repository.SnuttSemesterLectureRepository
import com.wafflestudio.snuttev.sync.service.SnuttLectureSyncJobService
import org.junit.jupiter.api.Assertions.*
import org.mockito.BDDMockito.given
import org.springframework.boot.test.mock.mockito.MockBean


@Deprecated("It is replaced by SnuttLectureSyncJobConfig")
class SnuttLectureSyncJobSliceTest(
    private val snuttLectureSyncJobService: SnuttLectureSyncJobService,
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
            classification = "교양",
            department = "",
            academicYear = "",
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

    val autumnSemesterLectures = listOf(
        makeTestSnuttSemesterLecture(
            year = 2021,
            semester = 3,
            courseNumber = "course1",
            lectureNumber = "lecture1",
            instructor = "instructor1",
            category = "categorya"
        ),
        makeTestSnuttSemesterLecture(
            year = 2021,
            semester = 3,
            courseNumber = "course1",
            lectureNumber = "lecture2",
            instructor = "instructor1",
            category = "categorya"
        ),
        makeTestSnuttSemesterLecture(
            year = 2021,
            semester = 3,
            courseNumber = "course2",
            lectureNumber = "lecture1",
            instructor = "instructor2",
            category = "categoryc"
        ),
        makeTestSnuttSemesterLecture(
            year = 2021,
            semester = 3,
            courseNumber = "course2",
            lectureNumber = "lecture2",
            instructor = "instructor1",
            category = "categoryd"
        ),
        makeTestSnuttSemesterLecture(
            year = 2021,
            semester = 3,
            courseNumber = "course3",
            lectureNumber = "lecture1",
            instructor = "instructor3",
            category = "categorye"
        ),
        makeTestSnuttSemesterLecture(
            year = 2021,
            semester = 3,
            courseNumber = "course3",
            lectureNumber = "lecture2",
            instructor = "instructor4",
            category = "categoryf"
        ),
    )

    val winterSemesterLectures = listOf(
        makeTestSnuttSemesterLecture(
            year = 2021,
            semester = 4,
            courseNumber = "course1",
            lectureNumber = "lecture1",
            instructor = "instructor1",
            category = "categoryb"
        ),
        makeTestSnuttSemesterLecture(
            year = 2021,
            semester = 4,
            courseNumber = "course1",
            lectureNumber = "lecture2",
            instructor = "instructor3",
            category = "categoryc"
        ),
        makeTestSnuttSemesterLecture(
            year = 2021,
            semester = 4,
            courseNumber = "course2",
            lectureNumber = "lecture1",
            instructor = "instructor3",
            category = "categoryd"
        ),
        makeTestSnuttSemesterLecture(
            year = 2021,
            semester = 4,
            courseNumber = "course2",
            lectureNumber = "lecture2",
            instructor = "instructor4",
            category = "categorye"
        ),
        makeTestSnuttSemesterLecture(
            year = 2021,
            semester = 4,
            courseNumber = "course5",
            lectureNumber = "lecture1",
            instructor = "instructor5",
            category = "categoryf"
        ),
        makeTestSnuttSemesterLecture(
            year = 2021,
            semester = 4,
            courseNumber = "course5",
            lectureNumber = "lecture1",
            instructor = "instructor2",
            category = "categoryg"
        ),
        makeTestSnuttSemesterLecture(
            year = 2021,
            semester = 4,
            courseNumber = "course6",
            lectureNumber = "lecture1",
            instructor = "instructor6",
            category = "categoryi"
        ),
    )

    fun `snutt 전체 강의 데이터 migration 시에 course번호와 교수이름이 중복되는 강의 데이터는 중복해 저장하지 않는다`() {
        given(snuttSemesterLectureRepository.findAll()).willReturn(autumnSemesterLectures + winterSemesterLectures)

        snuttLectureSyncJobService.migrateAllLectureDataFromSnutt()
        val lectures = lectureRepository.findAll()

        assertEquals(11, lectures.size)
    }

    fun `snutt 전체 강의 데이터 migration 시에 강의와 학기가 중복되는 데이터는 중복해 저장하지 않는다`() {
        given(snuttSemesterLectureRepository.findAll()).willReturn(autumnSemesterLectures + winterSemesterLectures)

        snuttLectureSyncJobService.migrateAllLectureDataFromSnutt()
        val semesterLectures = semesterLectureRepository.findAll()

        assertEquals(12, semesterLectures.size)
    }

    fun `snutt 최근 강의 데이터 migration시 강의, semesterLecture 중복 제거 테스트`() {
        given(semesterUtils.getCurrentYearAndSemester()).willReturn(2021 to Semester.SUMMER)
        given(semesterUtils.getYearAndSemesterOfNextSemester()).willReturn(2021 to Semester.AUTUMN)
        given(snuttSemesterLectureRepository.existsByYearAndSemester(2021, Semester.AUTUMN.value)).willReturn(true)
        given(snuttSemesterLectureRepository.findMongoSemesterLecturesByYearAndSemester(2021, Semester.AUTUMN.value))
            .willReturn(autumnSemesterLectures)

        snuttLectureSyncJobService.migrateLatestSemesterLectureDataFromSnutt()
        val lectures = lectureRepository.findAll()
        val semesterLectures = semesterLectureRepository.findAll()

        assertEquals(5, lectures.size )
        assertEquals(5, semesterLectures.size )
    }

    fun `snutt데이터 그대로일 때 sync 여러번 일어나도 기존 데이터 유지`() {
        given(snuttSemesterLectureRepository.findAll()).willReturn(autumnSemesterLectures + winterSemesterLectures)
        snuttLectureSyncJobService.migrateAllLectureDataFromSnutt()

        val semesterLecturesBefore = semesterLectureRepository.findAll()
        snuttLectureSyncJobService.migrateAllLectureDataFromSnutt()
        val semesterLecturesAfter = semesterLectureRepository.findAll()

        assertIterableEquals(semesterLecturesBefore, semesterLecturesAfter)
    }

    fun `기존에 들어있던 SemesterLecture에 lecture가 같은 semester가 업데이트 된 경우 정상 업데이트 작동 확인`() {

        given(snuttSemesterLectureRepository.findAll()).willReturn(
            listOf(
                makeTestSnuttSemesterLecture(
                    year = 2021,
                    semester = 3,
                    courseNumber = "course1",
                    lectureNumber = "lecture1",
                    instructor = "instructor1",
                    category = ""
                ),
            )
        )
        given(semesterUtils.getCurrentYearAndSemester()).willReturn(2021 to Semester.SUMMER)
        given(semesterUtils.getYearAndSemesterOfNextSemester()).willReturn(2021 to Semester.AUTUMN)
        given(snuttSemesterLectureRepository.existsByYearAndSemester(2021, Semester.AUTUMN.value)).willReturn(true)
        given(
            snuttSemesterLectureRepository.findMongoSemesterLecturesByYearAndSemester(
                2021,
                Semester.AUTUMN.value
            )
        ).willReturn(
            autumnSemesterLectures
        )

        snuttLectureSyncJobService.migrateAllLectureDataFromSnutt()
        snuttLectureSyncJobService.migrateLatestSemesterLectureDataFromSnutt()

        val targetSemesterLectures = semesterLectureRepository.findAll().first()

        assertEquals(targetSemesterLectures.category, "categorya")
    }

    fun `기존에 들어있던 lecture 데이터들 최신 학기의 강의 정보로 lecture 정보 최신화`() {
        given(snuttSemesterLectureRepository.findAll()).willReturn(autumnSemesterLectures)
        given(semesterUtils.getCurrentYearAndSemester()).willReturn(2021 to Semester.AUTUMN)
        given(semesterUtils.getYearAndSemesterOfNextSemester()).willReturn(2021 to Semester.WINTER)
        given(snuttSemesterLectureRepository.existsByYearAndSemester(2021, Semester.WINTER.value)).willReturn(true)
        given(
            snuttSemesterLectureRepository.findMongoSemesterLecturesByYearAndSemester(
                2021,
                Semester.WINTER.value
            )
        ).willReturn(
            winterSemesterLectures
        )

        snuttLectureSyncJobService.migrateAllLectureDataFromSnutt()

        val lectureBefore = lectureRepository.findByCourseNumberAndInstructor("course1", "instructor1")
        assertNotNull(lectureBefore)
        assertEquals(lectureBefore!!.category, "categorya")

        snuttLectureSyncJobService.migrateLatestSemesterLectureDataFromSnutt()

        val lectureAfter = lectureRepository.findByCourseNumberAndInstructor("course1", "instructor1")

        assertNotNull(lectureAfter)
        assertEquals(lectureAfter!!.category, "categoryb")
    }
}