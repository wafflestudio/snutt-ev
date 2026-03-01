package com.wafflestudio.snuttev.sync

import com.wafflestudio.snuttev.core.domain.lecture.repository.LectureRepository
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.Step
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.infrastructure.item.ItemWriter
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.orm.jpa.JpaTransactionManager

@Configuration
@Profile(value = ["!test"])
class SnuttRatingSyncJobConfig(
    private val entityManagerFactory: EntityManagerFactory,
    private val mongoTemplate: MongoTemplate,
    private val lectureRepository: LectureRepository,
) {
    companion object {
        const val RATING_SYNC_JOB_NAME = "RATING_SYNC_JOB"
        private const val CUSTOM_READER_JOB_STEP = RATING_SYNC_JOB_NAME + "_STEP"
        private const val CHUNK_SIZE = 1000
    }

    @Bean
    fun ratingSyncJob(jobRepository: JobRepository): Job =
        JobBuilder(RATING_SYNC_JOB_NAME, jobRepository)
            .start(customReaderStep(jobRepository))
            .build()

    private fun customReaderStep(jobRepository: JobRepository): Step =
        StepBuilder(CUSTOM_READER_JOB_STEP, jobRepository)
            .chunk<SnuttLectureRatingSyncTarget, SnuttLectureRatingSyncTarget>(CHUNK_SIZE)
            .transactionManager(
                JpaTransactionManager().apply {
                    this.entityManagerFactory = this@SnuttRatingSyncJobConfig.entityManagerFactory
                },
            ).reader(reader())
            .writer(writer())
            .build()

    private fun reader(): JpaPagingItemReader<SnuttLectureRatingSyncTarget> =
        JpaPagingItemReaderBuilder<SnuttLectureRatingSyncTarget>()
            .name("snuttLectureRatingSyncReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString(
                """
                SELECT new com.wafflestudio.snuttev.sync.SnuttLectureRatingSyncTarget(s.snuttId, sl.lecture.id)
                FROM SnuttLectureIdMap s
                JOIN s.semesterLecture sl
                ORDER BY s.id
                """.trimIndent(),
            ).pageSize(CHUNK_SIZE)
            .build()

    private fun writer(): ItemWriter<SnuttLectureRatingSyncTarget> =
        ItemWriter { items ->
            val lectureIdToLectureRatingMap =
                lectureRepository
                    .findAllRatingsByLectureIds(
                        items
                            .asSequence()
                            .map { it.lectureId }
                            .distinct()
                            .toList(),
                    ).associateBy { it.id }
            val bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "lectures")
            items.forEach {
                val evInfo = lectureIdToLectureRatingMap[it.lectureId]
                bulkOps.updateOne(
                    Query(Criteria.where("_id").`is`(it.snuttId)),
                    Update()
                        .set("evInfo.evId", evInfo?.id)
                        .set("evInfo.avgRating", evInfo?.avgRating)
                        .set("evInfo.count", evInfo?.count),
                )
            }
            bulkOps.execute()
        }
}

data class SnuttLectureRatingSyncTarget(
    val snuttId: String,
    val lectureId: Long,
)
