package de.doit.searchapi.domain.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.core.EntityMapper
import java.io.IOException

@Configuration
internal class ElasticsearchConfig {

    @Bean
    fun entityMapper(): EntityMapper {
        val objectMapper = ObjectMapper().apply {
            registerKotlinModule()

            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        }

        return CustomEntityMapper(objectMapper)
    }

    //TODO might be possible to remove when using kotlin jpa compiler plugin
    internal class CustomEntityMapper(private val objectMapper: ObjectMapper): EntityMapper {

        override fun <T : Any?> mapToObject(source: String?, clazz: Class<T>?): T {
            return objectMapper.readValue(source, clazz)
        }

        @Throws(IOException::class)
        override fun mapObject(source: Any?): MutableMap<String, Any> {
            return objectMapper.readValue(mapToString(source))
        }


        @Throws(IOException::class)
        override fun mapToString(`object`: Any?): String {
            return objectMapper.writeValueAsString(`object`)
        }

        @Throws(IOException::class)
        override fun <T : Any?> readObject(source: MutableMap<String, Any>?, targetType: Class<T>?): T? {
            return mapToObject(mapToString(source), targetType)
        }

    }

}