package com.wafflestudio.snuttev.core.common.type

import com.fasterxml.jackson.annotation.JsonValue

enum class LectureClassification(@get:JsonValue val value: String) {
    LIBERAL_EDUCATION("교양"),
    ELECTIVE_SUBJECT("전선"),
    REQUISITE_SUBJECT("전필"),
    ELECTIVE_GENERAL("일선"),
    READING_AND_RESEARCH("논문"),
    TEACHING_CERTIFICATION("교직"),
    GRADUATE("대학원"),
    CORE_SUBJECT("공통"),
    ;

    companion object {
        private val mapping = values().associateBy { e -> e.value }

        fun customValueOf(classification: String): LectureClassification? = mapping.getOrDefault(classification, null)
    }
}
