package de.doit.searchapi.index

import de.doit.jobapi.domain.event.JobAggregatedEvent
import de.doit.searchapi.domain.event.JobIndexEvent
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.assertj.core.api.Assertions.assertThat
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates
import org.jeasy.random.api.Randomizer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
internal class KafkaStreamsTest(@Autowired private val streamsBuilder: StreamsBuilder,
                                @Autowired private val streamsBuilderFactoryBean: StreamsBuilderFactoryBean,
                                @Autowired private val kafkaConfigProperties: KafkaConfigProperties) {

    private val easyRandom = EasyRandom(EasyRandomParameters().randomize(
            FieldPredicates.named("payment"), Randomizer { BigDecimal.valueOf(9.99) })
    )

    private lateinit var testDriver: TopologyTestDriver
    private lateinit var recordFactory: ConsumerRecordFactory<String, Any>
    private lateinit var specificAvroSerde: Serde<Any>

    @BeforeEach
    internal fun setUp() {
        testDriver = TopologyTestDriver(streamsBuilder.build(), streamsBuilderFactoryBean.streamsConfiguration )
        specificAvroSerde = streamsBuilderFactoryBean.streamsConfig.defaultValueSerde()
        recordFactory = ConsumerRecordFactory(StringSerializer(), specificAvroSerde.serializer())
    }

    @Test
    internal fun testMapping() {
        val (source, sink) = kafkaConfigProperties
        val jobAggregate = easyRandom.nextObject(JobAggregatedEvent::class.java)

        testDriver.pipeInput(recordFactory.create(source.topic, jobAggregate.getId(), jobAggregate))
        val jobIndexEvent = testDriver.readOutput(sink.topic, StringDeserializer(), specificAvroSerde.deserializer())

        assertThat(jobIndexEvent.key()).isEqualTo(jobAggregate.getId())
        assertThat(jobIndexEvent.value()).isInstanceOf(JobIndexEvent::class.java)
        with(jobIndexEvent.value() as JobIndexEvent) {
            assertThat(this.getVendorId()).isEqualTo(jobAggregate.getVendorId())
            assertThat(this.getTitle()).isEqualTo(jobAggregate.getTitle())
            assertThat(this.getDescription()).isEqualTo(jobAggregate.getDescription())
            assertThat(this.getPayment()).isEqualTo(jobAggregate.getPayment())
            assertThat(this.getCreatedAt()).isEqualTo(jobAggregate.getCreatedAt())
            assertThat(this.getModifiedAt()).isEqualTo(jobAggregate.getModifiedAt())
            assertThat(this.getLocation().getLat()).isEqualTo(jobAggregate.getLatitude())
            assertThat(this.getLocation().getLon()).isEqualTo(jobAggregate.getLongitude())
        }
    }

}