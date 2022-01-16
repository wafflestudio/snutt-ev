package com.wafflestudio.snuttev.scheduler.lecture_crawler

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


@Component
class SnuttLectureSyncJobScheduler(private val service: SnuttLectureSyncJobService) {

    //    전체 수강편람 옮기는 job, local인 경우에만 작동
    @PostConstruct
    @Profile("local")
    fun fetchAll() {
        print("local??===")
        service.migrateAllLectureDataFromSnutt()
    }

    //    매주 수요일 6:00
    @Scheduled(cron = "0 0 21 * * 2", zone = "UTC")
    @Profile("!test")
    fun fetchLatestWeekly() {
        service.migrateLatestSemesterLectureDataFromSnutt()
    }
}
