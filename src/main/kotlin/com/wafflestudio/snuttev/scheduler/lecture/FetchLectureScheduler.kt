package com.wafflestudio.snuttev.scheduler.lecture

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class FetchLectureScheduler(private val context: FetchLectureContext) {

    //    @PostConstruct
    fun fetchAllAtOnce() {
        context.migrateAllData()
    }

    // 매주 수요일 6:00
    @Scheduled(cron = "0 0 6 * * 3")
    fun fetchWeekly() {
        context.migrateCurrentSemesterData()
    }
}
