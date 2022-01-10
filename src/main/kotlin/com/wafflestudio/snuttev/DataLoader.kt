package com.wafflestudio.snuttev

import com.wafflestudio.snuttev.domain.common.Semester
import com.wafflestudio.snuttev.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.domain.lecture.repository.SemesterLectureRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("local")
class DataLoader(
    private val lectureRepository: LectureRepository,
    private val semesterLectureRepository: SemesterLectureRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val lecture = Lecture(
            title = "소프트웨어 개발의 원리와 실습",
            instructor = "전병곤",
            department = "컴퓨터공학부",
            courseNumber = "M1522.002400",
            credit = 4,
            academicYear = "3학년",
            category = "",
            classification = "전선",
        )
        lectureRepository.save(lecture)

        val semesterLecture = SemesterLecture(
            lecture = lecture,
            lectureNumber = "001",
            year = 2019,
            semester = Semester.AUTUMN.value,
            credit = 4,
            academicYear = "3학년",
            category = "",
            classification = "전필",
        )
        semesterLectureRepository.save(semesterLecture)
    }
}
