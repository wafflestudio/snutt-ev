package com.wafflestudio.snuttev.domain.lecture.repository

import com.wafflestudio.snuttev.domain.lecture.dto.SearchLectureResponse
import com.wafflestudio.snuttev.domain.lecture.dto.SearchQuery
import com.wafflestudio.snuttev.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LectureRepositoryCustom {
    fun searchLectures(request: SearchQuery, pageable: Pageable): Page<SearchLectureResponse>
    fun searchSemesterLectures(request: SearchQuery, pageable: Pageable): Page<SearchLectureResponse>
}
