package com.wafflestudio.snuttev.core.domain.evaluation.repository

import com.wafflestudio.snuttev.core.common.type.EvaluationSort
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationCursor
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationWithLectureDto
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationWithSemesterDto
import com.wafflestudio.snuttev.core.domain.tag.model.Tag

interface LectureEvaluationRepositoryCustom {
    fun findEvaluationWithSemesterById(
        id: Long,
        userId: String,
    ): EvaluationWithSemesterDto?

    fun findNotMyEvaluationsWithSemesterByLectureId(
        lectureId: Long,
        userId: String,
        cursor: EvaluationCursor?,
        pageSize: Int,
        sort: EvaluationSort = EvaluationSort.LATEST,
        year: Int? = null,
        semester: Int? = null,
    ): List<EvaluationWithSemesterDto>

    fun findMyEvaluationsWithSemesterByLectureId(
        lectureId: Long,
        userId: String,
    ): List<EvaluationWithSemesterDto>

    fun findMyEvaluationsWithLecture(
        userId: String,
        cursor: Long?,
        pageSize: Int,
    ): List<EvaluationWithLectureDto>

    fun findEvaluationWithLectureByTag(
        userId: String,
        tag: Tag,
        cursor: Long?,
        pageSize: Int,
    ): List<EvaluationWithLectureDto>
}
