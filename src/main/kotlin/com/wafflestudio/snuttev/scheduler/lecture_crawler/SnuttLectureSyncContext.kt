package com.wafflestudio.snuttev.scheduler.lecture_crawler

import com.wafflestudio.snuttev.common.Semester
import com.wafflestudio.snuttev.dao.model.Lecture
import com.wafflestudio.snuttev.dao.model.SemesterLecture
import com.wafflestudio.snuttev.dao.repository.LectureRepository
import com.wafflestudio.snuttev.dao.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.scheduler.lecture_crawler.model.SnuttSemesterLecture
import com.wafflestudio.snuttev.scheduler.lecture_crawler.repository.SnuttSemesterLectureRepository
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.Month
import javax.transaction.Transactional


@Component
class SnuttLectureSyncContext(
    private val snuttSemesterLectureRepository: SnuttSemesterLectureRepository,
    private val lectureRepository: LectureRepository,
    private val semesterLectureRepository: SemesterLectureRepository
) {
    @Transactional
    fun migrateAllLectureDataFromSnutt() {
        val snuttSemesterLectures = snuttSemesterLectureRepository.findAll()

        val existingSemesterLectures = semesterLectureRepository.findAll()

        migrateSemesterLecturesFromSnutt(snuttSemesterLectures, existingSemesterLectures)
    }

    @Transactional
    fun migrateLatestSemesterLectureDataFromSnutt() {
        val (currentYear, currentSemester) = getCurrentYearAndSemester()
        val (yearOfNextSemester, nextSemester) = getYearAndSemesterOfNextSemester()
        val (targetYear, targetSemester) = when (snuttSemesterLectureRepository.existsByYearAndSemester(
            yearOfNextSemester,
            nextSemester.raw
        )) {
            true -> yearOfNextSemester to nextSemester
            false -> currentYear to currentSemester
        }

        val latestSnuttSemesterLectures = snuttSemesterLectureRepository.findMongoSemesterLecturesByYearAndSemester(
            targetYear,
            targetSemester.raw
        )
        val existingSemesterLectures =
            semesterLectureRepository.findAllByYearAndSemester(targetYear, targetSemester.raw)

        migrateSemesterLecturesFromSnutt(latestSnuttSemesterLectures, existingSemesterLectures)
    }

    private fun getCurrentYearAndSemester(): Pair<Int, Semester> {
        val now = LocalDate.now()
        val year = now.year
        val semester = when {
            now.month < Month.MARCH -> Semester.WINTER
            now.month < Month.JULY -> Semester.SPRING
            now.month < Month.SEPTEMBER -> Semester.SUMMER
            else -> Semester.AUTUMN
        }
        return year to semester
    }

    private fun getYearAndSemesterOfNextSemester(): Pair<Int, Semester> {
        val (year, semester) = getCurrentYearAndSemester()
        return when (semester) {
            Semester.SPRING -> year to Semester.SUMMER
            Semester.SUMMER -> year to Semester.AUTUMN
            Semester.AUTUMN -> year to Semester.WINTER
            Semester.WINTER -> year + 1 to Semester.SPRING
        }
    }

    private fun migrateSemesterLecturesFromSnutt(
        snuttSemesterLectures: List<SnuttSemesterLecture>,
        existingSemesterLectures: List<SemesterLecture>
    ) {
        val mergedSemesterLectures =
            mergeNewSemesterLecturesWithOriginal(snuttSemesterLectures, existingSemesterLectures)

        semesterLectureRepository.saveAll(mergedSemesterLectures)
    }

    private fun mergeNewSemesterLecturesWithOriginal(
        snuttSemesterLectures: List<SnuttSemesterLecture>,
        existingSemesterLectures: List<SemesterLecture>
    ): List<SemesterLecture> {
        val lecturesMap = generateUpdatedLectureMapWIthSnuttSemesterLectures(snuttSemesterLectures)
        val existingSemesterLecturesMap = existingSemesterLectures.associateBy { semesterLectureKeyOf(it) }

        return snuttSemesterLectures.map {
            val lecture = lecturesMap[lectureKeyOf(it)]!!
            existingSemesterLecturesMap[semesterLectureKeyOf(it)]?.apply {
                this.academicYear = it.academic_year
                this.category = it.category
                this.classfication = it.classification
                this.extraInfo = it.remark
                this.lecture = lecture
                this.credit = it.credit
            } ?: createSemesterLectureFromSnuttSemesterLectureAndLecture(it, lecture)
        }
    }

    private fun generateUpdatedLectureMapWIthSnuttSemesterLectures(snuttSemesterLectures: List<SnuttSemesterLecture>): Map<String, Lecture> {
        val originalLecturesMap = lectureRepository.findAll()
            .associateBy { lectureKeyOf(it) }

        return snuttSemesterLectures.associate {
            lectureKeyOf(it) to (originalLecturesMap[lectureKeyOf(it)]?.apply {
                this.academicYear = it.academic_year
                this.credit = it.credit
                this.classfication = it.classification
                this.category = it.category
            } ?: createNewLectureFromSnuttSemesterLecture(it))
        }
    }

    private fun createNewLectureFromSnuttSemesterLecture(e: SnuttSemesterLecture): Lecture {
        return Lecture(
            e.courseTitle,
            e.instructor,
            e.department,
            e.courseNumber,
            e.credit,
            e.academic_year,
            e.category,
            e.classification
        )
    }

    private fun createSemesterLectureFromSnuttSemesterLectureAndLecture(
        e: SnuttSemesterLecture,
        lecture: Lecture
    ): SemesterLecture {
        return SemesterLecture(
            lecture,
            e.lectureNumber,
            e.year,
            e.semester,
            e.credit,
            e.remark,
            e.academic_year,
            e.category,
            e.classification
        )
    }

    fun lectureKeyOf(e: Lecture): String {
        return lectureKeyOf(e.courseNumber, e.instructor, e.department, e.title)
    }

    fun lectureKeyOf(e: SnuttSemesterLecture): String {
        return lectureKeyOf(e.courseNumber, e.instructor, e.department, e.courseTitle)
    }

    fun lectureKeyOf(courseNumber: String, instructor: String, department: String, title: String): String {
        return "${courseNumber},${instructor},${department},${title}"
    }

    fun semesterLectureKeyOf(snuttSemesterLecture: SnuttSemesterLecture): String {
        return semesterLectureKeyOf(
            snuttSemesterLecture.year,
            snuttSemesterLecture.semester,
            snuttSemesterLecture.courseNumber,
            snuttSemesterLecture.lectureNumber
        )
    }

    fun semesterLectureKeyOf(semesterLecture: SemesterLecture): String {
        return semesterLectureKeyOf(
            semesterLecture.year,
            semesterLecture.semester,
            semesterLecture.lecture.courseNumber,
            semesterLecture.lectureNumber
        )
    }

    fun semesterLectureKeyOf(year: Int, semester: Int, courseNumber: String, lectureNumber: String): String {
        return "${year},${semester},${courseNumber},${lectureNumber}"
    }
}
