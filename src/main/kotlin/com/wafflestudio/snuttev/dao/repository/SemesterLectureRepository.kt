package com.wafflestudio.snuttev.dao.repository

import com.wafflestudio.snuttev.dao.model.SemesterLecture
import org.springframework.data.jpa.repository.JpaRepository

interface SemesterLectureRepository : JpaRepository<SemesterLecture, Long> {

}