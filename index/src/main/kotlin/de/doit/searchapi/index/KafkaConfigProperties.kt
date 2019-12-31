package de.doit.searchapi.index

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("searchapi.index.kafka")
internal data class KafkaConfigProperties(val source: Source, val sink: Sink) {
    data class Source(val topic: String)
    data class Sink(val topic: String, val numberOfPartitions: Int, val replicationFactor: Short)
}