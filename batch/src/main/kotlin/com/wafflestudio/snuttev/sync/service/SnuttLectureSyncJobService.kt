package com.wafflestudio.snuttev.sync.service

import com.wafflestudio.snuttev.common.type.LectureClassification
import com.wafflestudio.snuttev.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.sync.SemesterUtils
import com.wafflestudio.snuttev.sync.model.SnuttSemesterLecture
import com.wafflestudio.snuttev.sync.repository.SnuttSemesterLectureRepository


@Deprecated("It is replaced by SnuttLectureSyncJobConfig")
class SnuttLectureSyncJobService(
    private val semesterUtils: SemesterUtils,
    private val snuttSemesterLectureRepository: SnuttSemesterLectureRepository,
    private val lectureRepository: LectureRepository,
    private val semesterLectureRepository: SemesterLectureRepository
) {
    fun migrateAllLectureDataFromSnutt() {
        val snuttSemesterLectures = snuttSemesterLectureRepository.findAll()
        val existingSemesterLectures = semesterLectureRepository.findAll()

        migrateSemesterLecturesFromSnutt(snuttSemesterLectures, existingSemesterLectures)
    }

    fun migrateLatestSemesterLectureDataFromSnutt() {
        val (currentYear, currentSemester) = semesterUtils.getCurrentYearAndSemester()
        val (yearOfNextSemester, nextSemester) = semesterUtils.getYearAndSemesterOfNextSemester()
        val (targetYear, targetSemester) = when (snuttSemesterLectureRepository.existsByYearAndSemester(
            yearOfNextSemester, nextSemester.value
        )) {
            true -> yearOfNextSemester to nextSemester
            false -> currentYear to currentSemester
        }

        val latestSnuttSemesterLectures = snuttSemesterLectureRepository.findMongoSemesterLecturesByYearAndSemester(
            targetYear, targetSemester.value
        ).filter { it.instructor.isNotEmpty() }
        val existingSemesterLectures =
            semesterLectureRepository.findAllByYearAndSemesterWithLecture(targetYear, targetSemester.value)

        migrateSemesterLecturesFromSnutt(latestSnuttSemesterLectures, existingSemesterLectures)
    }

    private fun migrateSemesterLecturesFromSnutt(
        newSnuttSemesterLectures: List<SnuttSemesterLecture>, existingSemesterLectures: List<SemesterLecture>
    ) {
        val mergedSemesterLectures = mergeNewSemesterLecturesWithExistingLectures(
            newSnuttSemesterLectures, existingSemesterLectures
        )

        val lectures = mergedSemesterLectures.map { it.lecture }.toSet()
        lectureRepository.saveAll(lectures)
        semesterLectureRepository.saveAll(mergedSemesterLectures)
    }

    private fun mergeNewSemesterLecturesWithExistingLectures(
        snuttSemesterLectures: List<SnuttSemesterLecture>, existingSemesterLectures: List<SemesterLecture>
    ): Collection<SemesterLecture> {
        val mergedLecturesMap: Map<String, Lecture> =
            mergeLecturesFromSemesterLecturesWithExistingLectures(snuttSemesterLectures)
        val existingSemesterLecturesMap: Map<String, SemesterLecture> =
            existingSemesterLectures.associateBy { semesterLectureKeyOf(it) }

        return snuttSemesterLectures.associate {
            val lecture = mergedLecturesMap[lectureKeyOf(it)]!!
            semesterLectureKeyOf(it) to (existingSemesterLecturesMap[semesterLectureKeyOf(it)]?.apply {
                this.academicYear = it.academicYear
                this.category = it.category
                this.classification = LectureClassification.customValueOf(it.classification)!!
                this.extraInfo = it.remark
                this.lecture = lecture
                this.credit = it.credit
            } ?: createSemesterLectureFromSnuttSemesterLectureAndLecture(it, lecture))
        }.values
    }

    private fun mergeLecturesFromSemesterLecturesWithExistingLectures(
        newSnuttSemesterLectures: List<SnuttSemesterLecture>
    ): Map<String, Lecture> {
        val originalLecturesMap = lectureRepository.findAll().associateBy { lectureKeyOf(it) }

        return newSnuttSemesterLectures.associate {
            lectureKeyOf(it) to (originalLecturesMap[lectureKeyOf(it)]?.apply {
                this.academicYear = it.academicYear
                this.credit = it.credit
                this.classification = LectureClassification.customValueOf(it.classification)!!
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
            e.academicYear,
            e.category,
            LectureClassification.customValueOf(e.classification)!!,
        )
    }

    private fun createSemesterLectureFromSnuttSemesterLectureAndLecture(
        e: SnuttSemesterLecture, lecture: Lecture
    ): SemesterLecture {
        return SemesterLecture(
            lecture,
            e.year,
            e.semester,
            e.credit,
            e.remark,
            e.academicYear,
            e.category,
            LectureClassification.customValueOf(e.classification)!!,
        )
    }

    private fun lectureKeyOf(e: Lecture): String {
        return lectureKeyOf(e.courseNumber, e.instructor)
    }

    private fun lectureKeyOf(e: SnuttSemesterLecture): String {
        return lectureKeyOf(e.courseNumber, e.instructor)
    }

    private fun lectureKeyOf(courseNumber: String, instructor: String): String {
        return "${courseNumber},${instructor}"
    }

    private fun semesterLectureKeyOf(snuttSemesterLecture: SnuttSemesterLecture): String {
        return semesterLectureKeyOf(
            snuttSemesterLecture.year,
            snuttSemesterLecture.semester,
            snuttSemesterLecture.courseNumber,
            snuttSemesterLecture.instructor
        )
    }

    private fun semesterLectureKeyOf(semesterLecture: SemesterLecture): String {
        return semesterLectureKeyOf(
            semesterLecture.year,
            semesterLecture.semester,
            semesterLecture.lecture.courseNumber,
            semesterLecture.lecture.instructor
        )
    }

    private fun semesterLectureKeyOf(year: Int, semester: Int, courseNumber: String, instructor: String): String {
        return "${year},${semester},${courseNumber},${instructor}"
    }
}
