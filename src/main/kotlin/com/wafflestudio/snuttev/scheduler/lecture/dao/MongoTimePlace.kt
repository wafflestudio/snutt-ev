package com.wafflestudio.snuttev.scheduler.lecture.dao

data class MongoTimePlace(
    val day: Int,
    val start: Int,
    val len: Int,
    val place: String
)