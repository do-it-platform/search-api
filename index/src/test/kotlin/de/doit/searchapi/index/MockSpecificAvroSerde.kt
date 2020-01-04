package de.doit.searchapi.index

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde

class MockSpecificAvroSerde<T : SpecificRecord>(private val delegate: SpecificAvroSerde<T>) : Serde<T> by delegate {
    constructor() : this(SpecificAvroSerde<T>(schemaRegistryClient)
            .apply {
                configure(mapOf(
                        SCHEMA_REGISTRY_URL_CONFIG to "http://mock:8081",
                        AUTO_REGISTER_SCHEMAS to true
                ), false)
            }
    )

    companion object {
        private val schemaRegistryClient = MockSchemaRegistryClient()
    }
}