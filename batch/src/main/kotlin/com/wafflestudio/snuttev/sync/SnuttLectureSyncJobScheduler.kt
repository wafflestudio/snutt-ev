package com.wafflestudio.snuttev.sync

import com.wafflestudio.snuttev.sync.service.SnuttLectureSyncJobService


//@Component
@Deprecated("Use SnuttLectureSyncJob",ReplaceWith("SnuttLectureSyncJob", "com.wafflestudio.snuttev.sync.SnuttLectureSyncJobConfig"))

class SnuttLectureSyncJobScheduler(private val service: SnuttLectureSyncJobService) {

    //    전체 수강편람 옮기는 job, local인 경우에만 작동
    fun fetchAll() {
        service.migrateAllLectureDataFromSnutt()
    }

    //    매주 수요일 6:00
    fun fetchLatestWeekly() {
        service.migrateLatestSemesterLectureDataFromSnutt()
    }
}
