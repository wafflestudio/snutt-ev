package com.wafflestudio.snuttev.scheduler.lecture.repository

import com.wafflestudio.snuttev.scheduler.lecture.dao.SnuttSemesterLecture
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SnuttSemesterLectureRepository : MongoRepository<SnuttSemesterLecture, String> {
    fun findMongoSemesterLecturesByYearAndSemester(year: Int, semester: Int): List<SnuttSemesterLecture>
}
