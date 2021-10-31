package com.wafflestudio.snuttev.scheduler.lecture_crawler

import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


@Component
class SnuttLectureSyncScheduler(private val context: SnuttLectureSyncContext) {

    //    전체 수강편람 옮기는 job, DB 초기화시에만 아래 주석을 제거하고 사용
//    @PostConstruct
    fun fetchAll() {
        context.migrateAllLectureDataFromSnutt()
    }

    //    매주 수요일 6:00
//    @Scheduled(cron = "0 0 6 * * 3")
    @PostConstruct
    fun fetchLatestWeekly() {
        context.migrateLatestSemesterLectureDataFromSnutt()
    }
}
