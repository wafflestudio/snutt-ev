package com.wafflestudio.snuttev.core.domain.lecture.repository

import com.wafflestudio.snuttev.core.common.dto.SearchQueryDto
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LectureRepositoryCustom {
    fun searchLectures(request: com.wafflestudio.snuttev.core.common.dto.SearchQueryDto, pageable: Pageable): Page<LectureDto>
    fun searchSemesterLectures(request: com.wafflestudio.snuttev.core.common.dto.SearchQueryDto, pageable: Pageable): Page<LectureDto>
}
