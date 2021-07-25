package com.wafflestudio.snuttev

import com.wafflestudio.snuttev.dao.model.Lecture
import com.wafflestudio.snuttev.dao.model.SemesterLecture
import com.wafflestudio.snuttev.dao.repository.LectureRepository
import com.wafflestudio.snuttev.dao.repository.SemesterLectureRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DataLoader(
    private val lectureRepository: LectureRepository,
    private val semesterLectureRepository: SemesterLectureRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val lecture = Lecture(
            title = "소프트웨어 개발의 원리와 실습",
            instructor = "전병곤",
            department = "컴퓨터공학부",
        )
        lectureRepository.save(lecture)

        val semesterLecture = SemesterLecture(
            lecture = lecture,
            year = 2019,
            semester = 3,
            credit = 4,
        )
        semesterLectureRepository.save(semesterLecture)
    }
}
