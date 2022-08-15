package com.wafflestudio.snuttev.snuev

import com.wafflestudio.snuttev.snuev.model.SnuevEvaluation
import com.wafflestudio.snuttev.core.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.core.domain.evaluation.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.core.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.core.domain.lecture.model.SemesterLecture
import com.wafflestudio.snuttev.core.domain.lecture.repository.SemesterLectureRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.orm.jpa.JpaTransactionManager
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
class SnuevMigrationJobConfig(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory,
    private val lectureEvaluationRepository: LectureEvaluationRepository,
    private val semesterLectureRepository: SemesterLectureRepository,
    private val lectureRepository: LectureRepository,
) {
    private val JOB_NAME = "SNUEV_MIGRATION_JOB"
    private val CUSTOM_READER_JOB_STEP = JOB_NAME + "_STEP"
    private val CHUNK_SIZE = 100
    private val snuevDataSource: DataSource = DataSourceBuilder
        .create()
        .driverClassName("org.postgresql.Driver")
        .url("jdbc:postgresql://localhost:5432/snuev")
        .username("postgres")
        .password("password").build()

    @Bean
    fun customReaderJob(): Job {
        return jobBuilderFactory.get(JOB_NAME)
            .start(customReaderStep())
            .build()
    }

    private fun customReaderStep(): Step {
        return stepBuilderFactory.get(CUSTOM_READER_JOB_STEP)
            .chunk<SnuevEvaluation, LectureEvaluation>(CHUNK_SIZE)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .transactionManager(JpaTransactionManager().apply {
                this.entityManagerFactory = this@SnuevMigrationJobConfig.entityManagerFactory
            })
            .build()
    }

    private fun reader(): JdbcCursorItemReader<SnuevEvaluation> {
        return JdbcCursorItemReaderBuilder<SnuevEvaluation>()
            .fetchSize(CHUNK_SIZE)
            .dataSource(snuevDataSource)
            .rowMapper(DataClassRowMapper(SnuevEvaluation::class.java))
            .name(this::reader.name)
            .sql(
                """
                    SELECT ev.comment, ev.score, ev.easiness, ev.grading, ev.created_at, pr.name AS instructor, se.year, se.season, c.code AS course_number
                    FROM evaluations ev
                    INNER JOIN lectures le ON ev.lecture_id = le.id
                    INNER JOIN professors pr ON pr.id  = le.professor_id
                    INNER JOIN semesters se ON ev.semester_id = se.id
                    INNER JOIN courses c ON c.id = le.course_id;
                """.trimIndent()
            )
            .build()
    }

    private fun processor(): ItemProcessor<SnuevEvaluation, LectureEvaluation> {
        return ItemProcessor<SnuevEvaluation, LectureEvaluation> { item: SnuevEvaluation ->
            val lecture = lectureRepository.findByCourseNumberAndInstructor(item.courseNumber, item.instructor)
                ?: return@ItemProcessor null
            val semesterLecture =
                semesterLectureRepository.findByYearAndSemesterAndLecture(item.year, item.season + 1, lecture)
                    ?: semesterLectureRepository.save(
                        SemesterLecture(
                            lecture,
                            item.year,
                            item.season + 1,
                            lecture.credit,
                            "",
                            lecture.academicYear,
                            lecture.category,
                            lecture.classification
                        )
                    )
            val userId = "62c1c0f2ccb19a00111d37af" //snuevUser
            LectureEvaluation(
                semesterLecture = semesterLecture,
                userId = userId,
                content = item.comment,
                gradeSatisfaction = ((item.grading + 1) / 2).toDouble(),
                teachingSkill = null,
                gains = null,
                lifeBalance = null,
                rating = ((item.score + 1) / 2).toDouble(),
                likeCount = 0,
                dislikeCount = 0,
                isHidden = false,
                isReported = false,
                fromSnuev = true,
                createdAt = item.createdAt,
            )
        }
    }

    private fun writer(): ItemWriter<LectureEvaluation> {
        return ItemWriter { items ->
            semesterLectureRepository.saveAll(items.map { it.semesterLecture })
            lectureEvaluationRepository.saveAll(items)
        }
    }
}


