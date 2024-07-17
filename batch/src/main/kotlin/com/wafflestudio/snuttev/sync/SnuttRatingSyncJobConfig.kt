package com.wafflestudio.snuttev.sync

import com.wafflestudio.snuttev.core.domain.lecture.model.LectureRatingDao
import com.wafflestudio.snuttev.core.domain.lecture.model.SnuttLectureIdMap
import com.wafflestudio.snuttev.core.domain.lecture.repository.LectureRepository
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
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
) {
    companion object {
        private const val JOB_NAME = "RATING_SYNC_JOB"
        private const val CUSTOM_READER_JOB_STEP = JOB_NAME + "_STEP"
        private const val CHUNK_SIZE = 1000
    }

    private var lectureIdtoLectureRatingMap: MutableMap<Long, LectureRatingDao> = mutableMapOf()

    @Bean
    fun ratingSyncJob(job: JobRepository, jobRepository: JobRepository, lectureRepository: LectureRepository): Job {
        lectureIdtoLectureRatingMap = lectureRepository.findAllRatings().associateBy { it.id }.toMutableMap()
        return JobBuilder(JOB_NAME, jobRepository)
            .start(customReaderStep(jobRepository))
            .build()
    }

    private fun customReaderStep(jobRepository: JobRepository): Step {
        return StepBuilder(CUSTOM_READER_JOB_STEP, jobRepository)
            .chunk<SnuttLectureIdMap, Pair<String, LectureRatingDao?>>(
                CHUNK_SIZE,
                JpaTransactionManager().apply {
                    this.entityManagerFactory = this@SnuttRatingSyncJobConfig.entityManagerFactory
                },
            )
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build()
    }

    private fun reader(): JpaPagingItemReader<SnuttLectureIdMap> =
        JpaPagingItemReaderBuilder<SnuttLectureIdMap>()
            .name("snuttLectureIdMapReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT s FROM SnuttLectureIdMap s")
            .pageSize(CHUNK_SIZE)
            .build()

    private fun processor(): ItemProcessor<SnuttLectureIdMap, Pair<String, LectureRatingDao?>> =
        ItemProcessor { item ->
            item.snuttId to lectureIdtoLectureRatingMap[item.semesterLecture.lecture.id]
        }

    private fun writer(): ItemWriter<Pair<String, LectureRatingDao?>> {
        return ItemWriter { items ->
            val bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "lectures")
            items.forEach {
                bulkOps.updateOne(
                    Query(Criteria.where("_id").`is`(it.first)),
                    Update().set("evInfo.evId", it.second?.id)
                        .set("evInfo.avgRating", it.second?.avgRating)
                        .set("evInfo.count", it.second?.count),
                )
            }
            bulkOps.execute()
        }
    }
}
