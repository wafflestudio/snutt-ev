package com.wafflestudio.snuttev

import com.wafflestudio.snuttev.common.Semester
import com.wafflestudio.snuttev.dao.model.Lecture
import com.wafflestudio.snuttev.dao.model.SemesterLecture
import com.wafflestudio.snuttev.dao.repository.LectureRepository
import com.wafflestudio.snuttev.dao.repository.SemesterLectureRepository
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
            classfication = "전선"
        )

        val semesterLecture = SemesterLecture(
            lecture = lecture,
            lectureNumber = "001",
            year = 2019,
            semester = Semester.AUTUMN.value,
            credit = 4,
            academicYear = "3학년",
            category = "",
            classfication = "전필"
        )
        semesterLectureRepository.save(semesterLecture)
    }
}
