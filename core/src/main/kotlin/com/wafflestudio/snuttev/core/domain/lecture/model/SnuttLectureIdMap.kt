package com.wafflestudio.snuttev.core.domain.lecture.model

import com.wafflestudio.snuttev.core.common.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class SnuttLectureIdMap(
    @Column(name = "snutt_id", columnDefinition = "char(24)")
    var snuttId: String,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_lecture_id", nullable = false, unique = true)
    var semesterLecture: SemesterLecture,
) : BaseEntity()
