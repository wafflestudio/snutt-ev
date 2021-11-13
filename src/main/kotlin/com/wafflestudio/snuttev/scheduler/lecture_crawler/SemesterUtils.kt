package com.wafflestudio.snuttev.scheduler.lecture_crawler

import com.wafflestudio.snuttev.common.Semester
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.Month

@Component
class SemesterUtils {
    fun getCurrentYearAndSemester(): Pair<Int, Semester> {
        val now = LocalDate.now()
        val year = now.year
        val semester = when {
            now.month < Month.MARCH -> Semester.WINTER
            now.month < Month.JULY -> Semester.SPRING
            now.month < Month.SEPTEMBER -> Semester.SUMMER
            else -> Semester.AUTUMN
        }
        return year to semester
    }

    fun getYearAndSemesterOfNextSemester(): Pair<Int, Semester> {
        val (year, semester) = getCurrentYearAndSemester()
        return when (semester) {
            Semester.SPRING -> year to Semester.SUMMER
            Semester.SUMMER -> year to Semester.AUTUMN
            Semester.AUTUMN -> year to Semester.WINTER
            Semester.WINTER -> year + 1 to Semester.SPRING
        }
    }
}
