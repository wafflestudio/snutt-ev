package com.wafflestudio.snuttev.core.domain.lecture.repository

import com.wafflestudio.snuttev.core.common.dto.SearchQueryDto
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureDto
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureRatingResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LectureRepositoryCustom {
    fun searchLectures(request: SearchQueryDto, pageable: Pageable): Page<LectureDto>
    fun searchSemesterLectures(request: SearchQueryDto, pageable: Pageable): Page<LectureDto>
    fun findLectureRatingsBySnuttIds(snuttIds: List<String>): List<LectureRatingResponse>
}
