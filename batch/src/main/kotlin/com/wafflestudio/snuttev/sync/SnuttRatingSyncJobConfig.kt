package com.wafflestudio.snuttev.sync

import com.wafflestudio.snuttev.core.domain.lecture.model.SnuttLectureIdMap
import com.wafflestudio.snuttev.core.domain.lecture.repository.LectureRepository
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
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
        private const val CHUNK_SIZE = 1000000
    }

    @Bean
    fun ratingSyncJob(jobRepository: JobRepository): Job {
        return JobBuilder(RATING_SYNC_JOB_NAME, jobRepository)
            .start(customReaderStep(jobRepository))
            .build()
    }

    private fun customReaderStep(jobRepository: JobRepository): Step {
        return StepBuilder(CUSTOM_READER_JOB_STEP, jobRepository)
            .chunk<SnuttLectureIdMap, SnuttLectureIdMap>(
                CHUNK_SIZE,
                JpaTransactionManager().apply {
                    this.entityManagerFactory = this@SnuttRatingSyncJobConfig.entityManagerFactory
                },
            )
            .reader(reader())
            .writer(writer())
            .build()
    }

    private fun reader(): JpaPagingItemReader<SnuttLectureIdMap> =
        JpaPagingItemReaderBuilder<SnuttLectureIdMap>()
            .name("snuttLectureIdMapReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT s FROM SnuttLectureIdMap s JOIN FETCH s.semesterLecture")
            .pageSize(CHUNK_SIZE)
            .build()

    private fun writer(): ItemWriter<SnuttLectureIdMap> {
        return ItemWriter { items ->
            val lectureIdtoLectureRatingMap =
                lectureRepository.findAllRatingsByLectureIds(
                    items.mapNotNull { it.semesterLecture.lecture.id },
                )
                    .associateBy { it.id }
            val bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "lectures")
            items.forEach {
                val evInfo = lectureIdtoLectureRatingMap[it.semesterLecture.lecture.id]
                bulkOps.updateOne(
                    Query(Criteria.where("_id").`is`(it.snuttId)),
                    Update().set("evInfo.evId", evInfo?.id)
                        .set("evInfo.avgRating", evInfo?.avgRating)
                        .set("evInfo.count", evInfo?.count),
                )
            }
            bulkOps.execute()
        }
    }
}
