package com.wafflestudio.snuttev.core.domain.mongo

import com.wafflestudio.snuttev.core.domain.lecture.model.LectureRatingDao
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service

@Service
class MongoService(
    private val mongoTemplate: MongoTemplate,
) {
    fun updateEvInfoToSnuttIds(snuttIds: List<String>, evInfo: LectureRatingDao?) =
        mongoTemplate.updateMulti(
            Query(Criteria.where("_id").`in`(snuttIds)),
            Update().set("evInfo.evId", evInfo?.id)
                .set("evInfo.avgRating", evInfo?.avgRating)
                .set("evInfo.count", evInfo?.count),
            "lectures",
        )
}
