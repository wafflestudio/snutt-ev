package com.wafflestudio.snuttev.lecture

import com.wafflestudio.snuttev.dto.SearchQueryDto
import com.wafflestudio.snuttev.dto.lecture.LectureDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LectureRepositoryCustom {
    fun searchLectures(request: SearchQueryDto, pageable: Pageable): Page<LectureDto>
    fun searchSemesterLectures(request: SearchQueryDto, pageable: Pageable): Page<LectureDto>
}
