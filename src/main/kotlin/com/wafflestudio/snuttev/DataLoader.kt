package com.wafflestudio.snuttev

import com.wafflestudio.snuttev.dao.model.SemesterLecture
import com.wafflestudio.snuttev.dao.repository.SemesterLectureRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DataLoader(
    private val semesterLectureRepository: SemesterLectureRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        val semesterLecture = SemesterLecture(
            title = "소프트웨어 개발의 원리와 실습",
            instructor = "전병곤",
            department = "컴퓨터공학부",
            year = 2019,
            semester = 3
        )

        semesterLectureRepository.save(semesterLecture)
    }
}
