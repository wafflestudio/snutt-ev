package com.wafflestudio.snuttev.domain.lecture.repository

import com.wafflestudio.snuttev.domain.lecture.dto.SearchLectureRequest
import com.wafflestudio.snuttev.domain.lecture.model.Lecture
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LectureRepositoryCustom {
    fun searchLectures(request: SearchLectureRequest, pageable: Pageable): Page<Lecture>
}
