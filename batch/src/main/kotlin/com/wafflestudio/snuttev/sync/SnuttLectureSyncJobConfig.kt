package com.wafflestudio.snuttev.sync

import com.wafflestudio.snuttev.common.type.LectureClassification
import com.wafflestudio.snuttev.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.sync.model.SnuttSemesterLecture
import com.wafflestudio.snuttev.sync.repository.SnuttSemesterLectureRepository
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
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.orm.jpa.JpaTransactionManager
import javax.persistence.EntityManagerFactory

@Configuration
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

        return jobBuilderFactory.get(NEXT_SEMESTER_JOB_NAME)
            .start(
                customReaderStep(
                    Query.query(
                        Criteria
                            .where("year").isEqualTo(targetYear)
                            .and("semester").isEqualTo(targetSemester)
                    )
                )
            )
            .build()
    }

    @Bean
    fun syncJob(): Job {
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
        return MongoItemReaderBuilder<SnuttSemesterLecture>().collection("lectures").query(query)
            .targetType(SnuttSemesterLecture::class.java).pageSize(CHUNK_SIZE)
            .name(this::reader.name)
            .build()
    }

    private fun processor(): ItemProcessor<SnuttSemesterLecture, SemesterLecture> {
        return ItemProcessor<SnuttSemesterLecture, SemesterLecture> { item: SnuttSemesterLecture ->
            val lecture = lectureRepository.findByCourseNumberAndInstructor(item.courseNumber, item.instructor)?.apply {
                this.academicYear = item.academic_year
                this.credit = item.credit
                this.classification = LectureClassification.customValueOf(item.classification)!!
                this.category = item.category
            } ?: lectureRepository.save(
                Lecture(
                    item.courseTitle,
                    item.instructor,
                    item.department,
                    item.courseNumber,
                    item.credit,
                    item.academic_year,
                    item.category,
                    LectureClassification.customValueOf(item.classification)!!,
                )
            )
            semesterLectureRepository.findByLectureAndYearAndSemester(lecture, item.year, item.semester)?.apply {
                this.academicYear = item.academic_year
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
                item.academic_year,
                item.category,
                LectureClassification.customValueOf(item.classification)!!,
            )
        }
    }

    private fun writer(): ItemWriter<SemesterLecture> {
        return ItemWriter { items ->
            lectureRepository.saveAll(items.map { it.lecture })
            semesterLectureRepository.saveAll(items)
        }
    }
}
