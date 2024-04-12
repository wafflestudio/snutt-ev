package com.wafflestudio.snuttev.core.domain.lecture.model

import com.wafflestudio.snuttev.core.common.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.OptimisticLock

@Entity
@DynamicUpdate
@Table(
    indexes = [Index(name = "snutt_lecture_id_map_snutt_id_index", columnList = "snutt_id", unique = true)],
)
class SnuttLectureIdMap(
    @Column(name = "snutt_id", columnDefinition = "char(24)")
    var snuttId: String,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_lecture_id", nullable = false)
    @OptimisticLock(excluded = true)
    var semesterLecture: SemesterLecture,
) : BaseEntity()
