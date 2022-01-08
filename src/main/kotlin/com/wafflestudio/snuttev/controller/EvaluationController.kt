package com.wafflestudio.snuttev.controller

import com.wafflestudio.snuttev.dto.CreateEvaluationRequest
import com.wafflestudio.snuttev.dto.GetSemesterLecturesResponse
import com.wafflestudio.snuttev.dto.LectureEvaluationDto
import com.wafflestudio.snuttev.service.EvaluationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class EvaluationController(
    private val evaluationService: EvaluationService
) {

    @PostMapping("/v1/semester-lectures/{id}/evaluations")
    fun createEvaluation(
        @PathVariable(value = "id") semesterLectureId: Long,
        @RequestBody @Valid createEvaluationRequest: CreateEvaluationRequest,
        @RequestAttribute(value = "UserId") userId: String
    ): LectureEvaluationDto {
        return evaluationService.createEvaluation(userId, semesterLectureId, createEvaluationRequest)
    }

    @GetMapping("/v1/lectures/{id}/semester-lectures")
    fun getSemesterLectures(
        @PathVariable(value = "id") lectureId: Long,
    ): GetSemesterLecturesResponse {
        return evaluationService.getSemesterLectures(lectureId)
    }

    @GetMapping("/v1/lectures/{id}/evaluations")
    fun getLectureEvaluation(
        @PathVariable(value = "id") lectureId: Long
    ): ResponseEntity<List<LectureEvaluationDto>> {
        return evaluationService.getLectureEvaluationsOfLecture(lectureId).let {
            ResponseEntity.ok(it)
        }
    }
}
