package com.wafflestudio.snuttev.config

import com.wafflestudio.snuttev.snuev.model.SnuevEvaluation
import com.wafflestudio.snuttev.sync.model.SnuttSemesterLecture
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.context.annotation.Configuration

@Configuration
@RegisterReflectionForBinding(
    SnuevEvaluation::class,
    SnuttSemesterLecture::class,
)
class BatchNativeImageConfig
