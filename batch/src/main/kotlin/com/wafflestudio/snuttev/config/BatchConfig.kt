package com.wafflestudio.snuttev.config

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class BatchConfig : DefaultBatchConfigurer() {
    // 배치잡의 상태를 DB에 쓰지 않고 메모리에서 관리
    override fun setDataSource(dataSource: DataSource) {
    }
}
