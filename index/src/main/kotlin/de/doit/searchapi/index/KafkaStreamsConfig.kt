package de.doit.searchapi.index

import de.doit.jobapi.domain.event.JobAggregatedEvent
import de.doit.searchapi.domain.event.JobIndexEvent
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafkaStreams

@Configuration
@EnableKafkaStreams
@EnableConfigurationProperties(KafkaConfigProperties::class)
internal class KafkaStreamsConfig(@Autowired private val kafkaConfigProperties: KafkaConfigProperties,
                                  @Autowired private val indexMapper: IndexMapper) {

    @Bean
    fun createJobIndexLogTopic(): NewTopic {
        val (_, sinkConfig) = kafkaConfigProperties
        return NewTopic(
                sinkConfig.topic,
                sinkConfig.numberOfPartitions,
                sinkConfig.replicationFactor
        )
    }

    @Bean
    fun jobIndexEventStream(streamsBuilder: StreamsBuilder): KStream<String, JobIndexEvent?>? {
        return streamsBuilder.stream<String, JobAggregatedEvent>(kafkaConfigProperties.source.topic)
                .mapValues { v: JobAggregatedEvent? -> v?.let { indexMapper.mapToIndexEvent(v) } }
                .through(kafkaConfigProperties.sink.topic)
    }

}