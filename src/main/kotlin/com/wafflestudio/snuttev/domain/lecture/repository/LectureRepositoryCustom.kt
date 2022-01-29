package com.wafflestudio.snuttev.domain.lecture.repository

import com.wafflestudio.snuttev.domain.lecture.dto.LectureDto
import com.wafflestudio.snuttev.domain.lecture.dto.SearchQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LectureRepositoryCustom {
    fun searchLectures(request: SearchQuery, pageable: Pageable): Page<LectureDto>
    fun searchSemesterLectures(request: SearchQuery, pageable: Pageable): Page<LectureDto>
}
