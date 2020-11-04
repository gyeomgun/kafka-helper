package com.hg.kafka.helper.domain.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["com.hg.kafka.helper.domain.repository"])
open class MongoConfig
