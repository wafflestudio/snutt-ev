package com.wafflestudio.snuttev.core.domain.lecture.repository

import com.wafflestudio.snuttev.core.domain.lecture.model.SnuttLectureIdMap
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SnuttLectureIdMapRepository : JpaRepository<SnuttLectureIdMap, Long> {
    @Query("SELECT ttm FROM SnuttLectureIdMap ttm JOIN FETCH ttm.semesterLecture WHERE ttm.snuttId IN :snuttIds")
    fun findAllWithSemesterLectureBySnuttIdIn(snuttIds: List<String>): List<SnuttLectureIdMap>
    fun findBySnuttId(snuttId: String): SnuttLectureIdMap?
}
