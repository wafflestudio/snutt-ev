package com.wafflestudio.snuttev.core.config

import com.wafflestudio.snuttev.core.common.dto.common.CursorPaginationResponse
import com.wafflestudio.snuttev.core.common.dto.common.ListResponse
import com.wafflestudio.snuttev.core.common.dto.common.PaginationResponse
import com.wafflestudio.snuttev.core.common.error.ErrorResponse
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationCursor
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationReportDto
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationWithLectureDto
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationWithLectureResponse
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationWithSemesterDto
import com.wafflestudio.snuttev.core.domain.evaluation.dto.EvaluationsResponse
import com.wafflestudio.snuttev.core.domain.evaluation.dto.LectureEvaluationDto
import com.wafflestudio.snuttev.core.domain.evaluation.dto.LectureEvaluationSummaryResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.EvLectureSummaryForSnutt
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureAndSemesterLecturesResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureDto
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureIdListResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureIdResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.LectureTakenByUserResponse
import com.wafflestudio.snuttev.core.domain.lecture.dto.SnuttLectureInfo
import com.wafflestudio.snuttev.core.domain.tag.dto.SearchTagResponse
import com.wafflestudio.snuttev.core.domain.tag.dto.TagGroupDto
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Configuration

@Configuration
@RegisterReflectionForBinding(
    ErrorResponse::class,
    ListResponse::class,
    PaginationResponse::class,
    CursorPaginationResponse::class,
    EvaluationCursor::class,
    EvaluationWithSemesterDto::class,
    EvaluationWithLectureDto::class,
    LectureEvaluationDto::class,
    LectureEvaluationSummaryResponse::class,
    EvaluationWithLectureResponse::class,
    EvaluationsResponse::class,
    EvaluationReportDto::class,
    LectureDto::class,
    LectureIdResponse::class,
    LectureIdListResponse::class,
    LectureAndSemesterLecturesResponse::class,
    LectureTakenByUserResponse::class,
    EvLectureSummaryForSnutt::class,
    SnuttLectureInfo::class,
    TagGroupDto::class,
    SearchTagResponse::class,
)
class NativeImageConfig
