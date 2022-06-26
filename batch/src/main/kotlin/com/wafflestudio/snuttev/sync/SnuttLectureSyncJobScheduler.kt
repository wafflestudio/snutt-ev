package com.wafflestudio.snuttev.sync

import com.wafflestudio.snuttev.sync.service.SnuttLectureSyncJobService
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


@Component
class SnuttLectureSyncJobScheduler(private val service: SnuttLectureSyncJobService) {

    //    전체 수강편람 옮기는 job, local인 경우에만 작동
//    @PostConstruct
    fun fetchAll() {
        service.migrateAllLectureDataFromSnutt()
    }

    //    매주 수요일 6:00
    @PostConstruct
    fun fetchLatestWeekly() {
        service.migrateLatestSemesterLectureDataFromSnutt()
    }
}
