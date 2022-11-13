package com.wafflestudio.snuttev.core.domain.evaluation.repository

import com.wafflestudio.snuttev.core.common.type.LectureClassification
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationCursor
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationWithLectureDto
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationWithSemesterDto
import com.wafflestudio.snuttev.core.domain.tag.model.Tag

interface LectureEvaluationRepositoryCustom {

    fun findNotMyEvaluationsWithSemesterByLectureId(
        lectureId: Long,
        userId: String,
        cursor: EvaluationCursor?,
        pageSize: Int,
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

    fun findEvaluationWithLectureByTagAndClassification(
        tag: Tag,
        classification: LectureClassification,
        cursor: Long?,
        pageSize: Int,
    ): List<EvaluationWithLectureDto>
}
