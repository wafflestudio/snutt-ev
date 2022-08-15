package com.wafflestudio.snuttev.sync

import com.wafflestudio.snuttev.sync.repository.SnuttSemesterLectureRepository
import com.wafflestudio.snuttev.sync.model.SnuttSemesterLecture
import com.wafflestudio.snuttev.core.common.type.LectureClassification
import com.wafflestudio.snuttev.core.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.core.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.core.domain.lecture.repository.SemesterLectureRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.data.MongoItemReader
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.orm.jpa.JpaTransactionManager
import javax.persistence.EntityManagerFactory

@Configuration
@Profile(value = ["!test"])
class SnuttLectureSyncJobConfig(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory,
    private val mongoTemplate: MongoTemplate,
    private val semesterLectureRepository: SemesterLectureRepository,
    private val lectureRepository: LectureRepository,
    private val semesterUtils: SemesterUtils,
    private val snuttSemesterLectureRepository: SnuttSemesterLectureRepository,
) {
    private val JOB_NAME = "SYNC_JOB"
    private val NEXT_SEMESTER_JOB_NAME = "NEXT_SEMESTER_SYNC_JOB"
    private val CUSTOM_READER_JOB_STEP = JOB_NAME + "_STEP"
    private final val CHUNK_SIZE = 100

    private var lecturesMap: MutableMap<String, Lecture> = mutableMapOf()
    private var semesterLecturesMap: MutableMap<String, SemesterLecture> = mutableMapOf()

    @Bean
    fun syncJobNextSemester(): Job {
        val (currentYear, currentSemester) = semesterUtils.getCurrentYearAndSemester()
        val (yearOfNextSemester, nextSemester) = semesterUtils.getYearAndSemesterOfNextSemester()
        val (targetYear, targetSemester) = when (snuttSemesterLectureRepository.existsByYearAndSemester(
            yearOfNextSemester, nextSemester.value
        )) {
            true -> yearOfNextSemester to nextSemester
            false -> currentYear to currentSemester
        }
        lecturesMap = lectureRepository.findAll().associateBy { "${it.courseNumber},${it.instructor}" }.toMutableMap()
        semesterLecturesMap =
            semesterLectureRepository.findAllByYearAndSemesterWithLecture(targetYear, targetSemester.value)
                .associateBy { "${it.lecture.courseNumber},${it.lecture.instructor},${it.year},${it.semester}" }
                .toMutableMap()

        return jobBuilderFactory.get(NEXT_SEMESTER_JOB_NAME)
            .start(
                customReaderStep(
                    Query.query(
                        Criteria
                            .where("year").isEqualTo(targetYear)
                            .and("semester").isEqualTo(targetSemester.value)
                    )
                )
            )
            .build()
    }

    @Bean
    fun syncJob(): Job {
        lecturesMap = lectureRepository.findAll().associateBy { "${it.courseNumber},${it.instructor}" }.toMutableMap()
        semesterLecturesMap =
            semesterLectureRepository.findAllWithLecture()
                .associateBy { "${it.lecture.courseNumber},${it.lecture.instructor},${it.year},${it.semester}" }
                .toMutableMap()
        return jobBuilderFactory.get(JOB_NAME)
            .start(customReaderStep(Query()))
            .build()
    }

    private fun customReaderStep(query: Query): Step {
        return stepBuilderFactory.get(CUSTOM_READER_JOB_STEP)
            .chunk<SnuttSemesterLecture, SemesterLecture>(CHUNK_SIZE)
            .reader(reader(query))
            .processor(processor())
            .writer(writer())
            .transactionManager(JpaTransactionManager().apply {
                this.entityManagerFactory = this@SnuttLectureSyncJobConfig.entityManagerFactory
            })
            .build()
    }

    private fun reader(query: Query): MongoItemReader<SnuttSemesterLecture> {
        return MongoItemReaderBuilder<SnuttSemesterLecture>()
            .template(mongoTemplate)
            .collection("lectures").query(query)
            .sorts(mapOf("courseNumber" to Sort.DEFAULT_DIRECTION))
            .targetType(SnuttSemesterLecture::class.java).pageSize(CHUNK_SIZE)
            .name(this::reader.name)
            .build()
    }

    private fun processor(): ItemProcessor<SnuttSemesterLecture, SemesterLecture> {
        return ItemProcessor<SnuttSemesterLecture, SemesterLecture> { item: SnuttSemesterLecture ->
            val lecture: Lecture = lecturesMap["${item.courseNumber},${item.instructor}"]?.apply {
                this.academicYear = item.academicYear
                this.credit = item.credit
                this.classification = LectureClassification.customValueOf(item.classification)!!
                this.category = item.category
            } ?: Lecture(
                item.courseTitle,
                item.instructor,
                item.department,
                item.courseNumber,
                item.credit,
                item.academicYear,
                item.category,
                LectureClassification.customValueOf(item.classification)!!,
            ).also { lecturesMap["${item.courseNumber},${item.instructor}"] = it }
            semesterLecturesMap["${item.courseNumber},${item.instructor},${item.year},${item.semester}"]?.apply {
                this.academicYear = item.academicYear
                this.category = item.category
                this.classification = LectureClassification.customValueOf(item.classification)!!
                this.extraInfo = item.remark
                this.lecture = lecture
                this.credit = item.credit
            } ?: SemesterLecture(
                lecture,
                item.year,
                item.semester,
                item.credit,
                item.remark,
                item.academicYear,
                item.category,
                LectureClassification.customValueOf(item.classification)!!,
            ).also { semesterLecturesMap["${item.courseNumber},${item.instructor},${item.year},${item.semester}"] = it }
        }
    }

    private fun writer(): ItemWriter<SemesterLecture> {
        return ItemWriter { items ->
            lectureRepository.saveAll(items.map { it.lecture }.toSet())
            semesterLectureRepository.saveAll(items.toSet())
        }
    }
}
