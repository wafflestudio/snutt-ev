package com.wafflestudio.snuttev.scheduler.lecture

import com.wafflestudio.snuttev.common.Semester
import com.wafflestudio.snuttev.dao.model.Lecture
import com.wafflestudio.snuttev.dao.model.SemesterLecture
import com.wafflestudio.snuttev.dao.repository.LectureRepository
import com.wafflestudio.snuttev.dao.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.scheduler.lecture.dao.SnuttSemesterLecture
import com.wafflestudio.snuttev.scheduler.lecture.repository.SnuttSemesterLectureRepository
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.Month
import javax.transaction.Transactional


@Component
class FetchLectureContext(
    private val snuttSemesterLectureRepository: SnuttSemesterLectureRepository,
    private val lectureRepository: LectureRepository,
    private val semesterLectureRepository: SemesterLectureRepository
) {
    @Transactional
    fun migrateAllData() {
        val snuttSemesterLecture = snuttSemesterLectureRepository.findAll()

        migrateSemesterLectureFromSnuttToSnuttev(snuttSemesterLecture)
    }


    @Transactional
    fun migrateCurrentSemesterData() {
        val (currentYear, currentSemester) = getCurrentSemester()
        val (nextYear, nextSemester) = getNextSemester()
        val nextSemesterLectures =
            snuttSemesterLectureRepository.findMongoSemesterLecturesByYearAndSemester(nextYear, nextSemester.raw)
        val latestSemesterLectures = nextSemesterLectures.ifEmpty {
            snuttSemesterLectureRepository.findMongoSemesterLecturesByYearAndSemester(currentYear, currentSemester.raw)
        }

        migrateSemesterLectureFromSnuttToSnuttev(latestSemesterLectures)
    }

    private fun migrateSemesterLectureFromSnuttToSnuttev(semesterLectures: List<SnuttSemesterLecture>) {
        //        semesterLectures.forEach {
//            val lecture: Lecture =
//                lectureRepository.findByCourseNumberAndInstructorAndTitleAndDepartment(
//                    it.course_Int,
//                    it.instructor,
//                    it.course_title,
//                    it.department
//                ) ?: convertMongoSemesterLectureToLecture(it)
//            semesterLectureRepository.save(convertMongoSemesterLectureToSemesterLecture(it, lecture))
//        }
        fun keyOf(e: Lecture): String {
            return "${e.courseNumber},${e.instructor},${e.department},${e.title}"
        }

        fun keyOf(e: SnuttSemesterLecture): String {
            return "${e.course_Int ?: ""},${e.instructor},${e.department},${e.course_title}"
        }

        val originalLecturesMap = lectureRepository.findAll()
            .associateBy { keyOf(it) }
        val newLecturesMap = semesterLectures.associate {
            keyOf(it) to convertMongoSemesterLectureToLecture(it)
        }
        val lecturesAll = newLecturesMap + originalLecturesMap
        val snuttevSemesterLectures = semesterLectures.map {
            convertMongoSemesterLectureToSemesterLecture(
                it,
                lecturesAll.getOrDefault(keyOf(it), Lecture("", "", "", ""))
            )
        }
        semesterLectureRepository.saveAll(snuttevSemesterLectures)
    }

    private fun getCurrentSemester(): Pair<Int, Semester> {
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

    private fun getNextSemester(): Pair<Int, Semester> {
        val (year, semester) = getCurrentSemester()
        val nextYear = year + 1
        val nextSemester = when (semester) {
            Semester.SPRING -> Semester.SUMMER
            Semester.SUMMER -> Semester.AUTUMN
            Semester.AUTUMN -> Semester.WINTER
            Semester.WINTER -> Semester.SPRING
        }
        return nextYear to nextSemester
    }

    private fun convertMongoSemesterLectureToLecture(e: SnuttSemesterLecture): Lecture {
        return Lecture(
            e.course_title,
            e.instructor,
            e.department,
            e.course_Int ?: "",
        )
    }

    private fun convertMongoSemesterLectureToSemesterLecture(
        e: SnuttSemesterLecture,
        lecture: Lecture
    ): SemesterLecture {
        return SemesterLecture(
            lecture,
            e.year,
            e.semester,
            e.credit,
            e.remark
        )
    }
}
