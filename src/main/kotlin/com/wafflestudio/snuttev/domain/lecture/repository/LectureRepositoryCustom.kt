package com.wafflestudio.snuttev.domain.lecture.repository

import com.wafflestudio.snuttev.domain.lecture.dto.LectureDto
import com.wafflestudio.snuttev.domain.lecture.service.SearchQueryDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LectureRepositoryCustom {
    fun searchLectures(request: SearchQueryDto, pageable: Pageable): Page<LectureDto>
    fun searchSemesterLectures(request: SearchQueryDto, pageable: Pageable): Page<LectureDto>
}
