package com.wafflestudio.snuttev.sync

import com.wafflestudio.snuttev.core.common.type.LectureClassification
import com.wafflestudio.snuttev.core.common.util.SemesterUtils
import com.wafflestudio.snuttev.core.domain.lecture.model.Lecture
import com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.core.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.core.domain.lecture.repository.SemesterLectureRepository
import com.wafflestudio.snuttev.sync.model.SnuttSemesterLecture
import com.wafflestudio.snuttev.sync.repository.SnuttSemesterLectureRepository
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.data.MongoCursorItemReader
import org.springframework.batch.item.data.builder.MongoCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.orm.jpa.JpaTransactionManager

@Configuration
@Profile(value = ["!test"])
class SnuttLectureSyncJobConfig(
    private val entityManagerFactory: EntityManagerFactory,
    private val mongoTemplate: MongoTemplate,
    private val semesterLectureRepository: SemesterLectureRepository,
    private val lectureRepository: LectureRepository,
) {
    companion object {
        private const val JOB_NAME = "SYNC_JOB"
        private const val NEXT_SEMESTER_JOB_NAME = "NEXT_SEMESTER_SYNC_JOB"
        private const val CUSTOM_READER_JOB_STEP = JOB_NAME + "_STEP"
        private const val CHUNK_SIZE = 100
    }

    private var lecturesMap: MutableMap<String, Lecture> = mutableMapOf()
    private var semesterLecturesMap: MutableMap<String, SemesterLecture> = mutableMapOf()

    @Bean
    fun syncJobNextSemester(jobRepository: JobRepository): Job {
        val coursebook = mongoTemplate.findOne<Map<String, Any>>(
            Query().with(Sort.by(Sort.Direction.DESC, "year").and(Sort.by(Sort.Direction.DESC, "semester"))),
            "coursebooks",
        )
        val (targetYear, targetSemester) = coursebook!!.let {
            it["year"]!! as Int to it["semester"]!! as Int
        }
        lecturesMap = lectureRepository.findAll().associateBy { "${it.courseNumber},${it.instructor}" }.toMutableMap()
        semesterLecturesMap =
            semesterLectureRepository.findAllByYearAndSemesterWithLecture(targetYear, targetSemester)
                .associateBy { "${it.lecture.courseNumber},${it.lecture.instructor},${it.year},${it.semester}" }
                .toMutableMap()

        return JobBuilder(NEXT_SEMESTER_JOB_NAME, jobRepository)
            .start(
                customReaderStep(
                    jobRepository,
                    Query.query(
                        Criteria
                            .where("year").isEqualTo(targetYear)
                            .and("semester").isEqualTo(targetSemester),
                    ),
                ),
            )
            .build()
    }

    @Bean
    fun syncJob(jobRepository: JobRepository): Job {
        lecturesMap = lectureRepository.findAll().associateBy { "${it.courseNumber},${it.instructor}" }.toMutableMap()
        semesterLecturesMap =
            semesterLectureRepository.findAllWithLecture()
                .associateBy { "${it.lecture.courseNumber},${it.lecture.instructor},${it.year},${it.semester}" }
                .toMutableMap()
        return JobBuilder(JOB_NAME, jobRepository)
            .start(customReaderStep(jobRepository, Query()))
            .build()
    }

    private fun customReaderStep(jobRepository: JobRepository, query: Query): Step {
        return StepBuilder(CUSTOM_READER_JOB_STEP, jobRepository)
            .chunk<SnuttSemesterLecture, SemesterLecture>(
                CHUNK_SIZE,
                JpaTransactionManager().apply {
                    this.entityManagerFactory = this@SnuttLectureSyncJobConfig.entityManagerFactory
                },
            )
            .reader(reader(query))
            .processor(processor())
            .writer(writer())
            .build()
    }

    private fun reader(query: Query): MongoCursorItemReader<SnuttSemesterLecture> {
        return MongoCursorItemReaderBuilder<SnuttSemesterLecture>()
            .template(mongoTemplate)
            .collection("lectures").query(query)
            .sorts(mapOf("_id" to Sort.DEFAULT_DIRECTION))
            .targetType(SnuttSemesterLecture::class.java)
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
