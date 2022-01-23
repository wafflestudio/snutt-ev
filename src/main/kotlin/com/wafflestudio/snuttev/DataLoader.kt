package com.wafflestudio.snuttev

import com.wafflestudio.snuttev.domain.common.Semester
import com.wafflestudio.snuttev.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.domain.tag.model.Tag
import com.wafflestudio.snuttev.domain.tag.model.TagGroup
import com.wafflestudio.snuttev.domain.tag.model.TagValueType
import com.wafflestudio.snuttev.domain.tag.repository.TagGroupRepository
import com.wafflestudio.snuttev.domain.tag.repository.TagRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("local")
class DataLoader(
    private val lectureRepository: LectureRepository,
    private val semesterLectureRepository: SemesterLectureRepository,
    private val tagGroupRepository: TagGroupRepository,
    private val tagRepository: TagRepository,
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val lecture = Lecture(
            title = "소프트웨어 개발의 원리와 실습",
            instructor = "전병곤",
            department = "컴퓨터공학부",
            courseNumber = "M1522.002400",
            credit = 4,
            academicYear = "3학년",
            category = "",
            classification = "전선",
        )
        lectureRepository.save(lecture)

        val semesterLectures = listOf(
            SemesterLecture(
                lecture = lecture,
                lectureNumber = "001",
                year = 2019,
                semester = Semester.AUTUMN.value,
                credit = 4,
                academicYear = "3학년",
                category = "",
                classification = "전필",
            ),
            SemesterLecture(
                lecture = lecture,
                lectureNumber = "001",
                year = 2019,
                semester = Semester.SPRING.value,
                credit = 4,
                academicYear = "3학년",
                category = "",
                classification = "전필",
            ),
            SemesterLecture(
                lecture = lecture,
                lectureNumber = "001",
                year = 2020,
                semester = Semester.AUTUMN.value,
                credit = 4,
                academicYear = "3학년",
                category = "",
                classification = "전필",
            ),
            SemesterLecture(
                lecture = lecture,
                lectureNumber = "001",
                year = 2020,
                semester = Semester.SPRING.value,
                credit = 4,
                academicYear = "3학년",
                category = "",
                classification = "전필",
            ),
        )
        semesterLectureRepository.saveAll(semesterLectures)

        val mainTagGroup = TagGroup(
            name = "main",
            ordering = -1,
            valueType = TagValueType.LOGIC,
        )
        val academicYearTagGroup = TagGroup(
            name = "학년",
            ordering = 1,
            valueType = TagValueType.STRING,
        )
        val creditTagGroup = TagGroup(
            name = "학점",
            ordering = 2,
            valueType = TagValueType.INT,
        )
        val tagGroups = listOf(
            mainTagGroup,
            academicYearTagGroup,
            creditTagGroup,
        )
        tagGroupRepository.saveAll(tagGroups)
        val tags = listOf(
            Tag(
                tagGroup = mainTagGroup,
                name = "추천",
                ordering = 1,
            ),
            Tag(
                tagGroup = mainTagGroup,
                name = "명강",
                ordering = 2,
            ),
            Tag(
                tagGroup = mainTagGroup,
                name = "꿀강",
                ordering = 3,
            ),
            Tag(
                tagGroup = mainTagGroup,
                name = "고진감래",
                ordering = 4,
            ),
            Tag(
                tagGroup = academicYearTagGroup,
                name = "1학년",
                ordering = 1,
                stringValue = "1학년"
            ),
            Tag(
                tagGroup = academicYearTagGroup,
                name = "2학년",
                ordering = 2,
                stringValue = "2학년"
            ),
            Tag(
                tagGroup = academicYearTagGroup,
                name = "3학년",
                ordering = 3,
                stringValue = "3학년"
            ),
            Tag(
                tagGroup = academicYearTagGroup,
                name = "4학년",
                ordering = 4,
                stringValue = "4학년"
            ),
            Tag(
                tagGroup = creditTagGroup,
                name = "1학점",
                ordering = 1,
                intValue = 1,
            ),
            Tag(
                tagGroup = creditTagGroup,
                name = "2학점",
                ordering = 2,
                intValue = 2,
            ),
            Tag(
                tagGroup = creditTagGroup,
                name = "3학점",
                ordering = 3,
                intValue = 3,
            ),
            Tag(
                tagGroup = creditTagGroup,
                name = "4학점",
                ordering = 4,
                intValue = 4,
            ),
        )
        tagRepository.saveAll(tags)
    }
}
