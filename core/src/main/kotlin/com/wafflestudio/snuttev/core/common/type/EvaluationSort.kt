package com.wafflestudio.snuttev.core.common.type

import com.wafflestudio.snuttev.core.common.error.InvalidEvaluationSortException

enum class EvaluationSort {
    LATEST,
    RECOMMENDED,
    ;

    companion object {
        fun fromParameter(value: String?): EvaluationSort =
            when (value?.lowercase()) {
                null, "", "latest" -> LATEST
                "recommended" -> RECOMMENDED
                else -> throw InvalidEvaluationSortException
            }
    }
}
