package com.wafflestudio.snuttev.snuev.model

import java.time.LocalDateTime

data class SnuevEvaluation(
    val comment: String,
    val score: Int,
    val easiness: Int,
    val grading: Int,
    val createdAt: LocalDateTime,
    val instructor: String,
    val year: Int,
    val season: Int,
    val courseNumber: String,
)
