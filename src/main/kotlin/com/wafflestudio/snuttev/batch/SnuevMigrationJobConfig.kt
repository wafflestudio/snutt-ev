package com.wafflestudio.snuttev.batch

import com.wafflestudio.snuttev.domain.evaluation.model.LectureEvaluation
import com.wafflestudio.snuttev.domain.evaluation.repository.LectureEvaluationRepository
import com.wafflestudio.snuttev.domain.lecture.repository.LectureRepository
import com.wafflestudio.snuttev.domain.lecture.repository.SemesterLectureRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.DataClassRowMapper
import javax.sql.DataSource

@Configuration
class SnuevMigrationJobConfig(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val lectureEvaluationRepository: LectureEvaluationRepository,
    private val semesterLectureRepository: SemesterLectureRepository,
    private val lectureRepository: LectureRepository,
) {
    private final val JOB_NAME = "SNUEV_MIGRATION_JOB"
    private final val CUSTOM_READER_JOB_STEP = JOB_NAME + "_STEP"
    private final val CHUNK_SIZE = 100
    private final val snuevDataSource: DataSource = DataSourceBuilder
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

    @Bean
    fun customReaderStep(): Step {
        return stepBuilderFactory.get(CUSTOM_READER_JOB_STEP)
            .chunk<SnuevEvaluation, LectureEvaluation>(CHUNK_SIZE)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build()
    }

    @Bean
    @StepScope
    fun reader(): JdbcCursorItemReader<SnuevEvaluation> {
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

    @Bean
    fun processor(): ItemProcessor<SnuevEvaluation, LectureEvaluation> {
        return ItemProcessor<SnuevEvaluation, LectureEvaluation> { item: SnuevEvaluation ->
            val lecture = lectureRepository.findByCourseNumberAndInstructor(item.courseNumber, item.instructor)
                ?: return@ItemProcessor null
            val semesterLecture =
                semesterLectureRepository.findFirstByYearAndSemesterAndLecture(item.year, item.season, lecture)
                    ?: return@ItemProcessor null
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

    @Bean
    fun writer(): ItemWriter<LectureEvaluation> {
        return ItemWriter { items ->
            lectureEvaluationRepository.saveAll(items)
        }
    }
}
