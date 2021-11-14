package com.wafflestudio.snuttev.scheduler.lecture_crawler.repository

import com.wafflestudio.snuttev.scheduler.lecture_crawler.model.SnuttSemesterLecture
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SnuttSemesterLectureRepository : MongoRepository<SnuttSemesterLecture, String> {
    fun existsByYearAndSemester(year: Int, semester: Int): Boolean
    fun findMongoSemesterLecturesByYearAndSemester(year: Int, semester: Int): List<SnuttSemesterLecture>
}
