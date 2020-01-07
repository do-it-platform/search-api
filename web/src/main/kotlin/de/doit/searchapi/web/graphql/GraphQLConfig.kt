package de.doit.searchapi.web.graphql

import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
internal class GraphQLConfig(@Autowired private val jobDataFetcher: JobDataFetcher,
                             @Value("classpath:schema.graphqls") private val schema: Resource) {

    @Bean
    fun graphqlSchema(): GraphQLSchema {
        val parsedSchema = SchemaParser().parse(schema.file)
        return SchemaGenerator().makeExecutableSchema(parsedSchema, buildWiring())
    }

    private fun buildWiring(): RuntimeWiring {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query") {
                    it.dataFetcher("job", jobDataFetcher.job())
                    it.dataFetcher("jobs", jobDataFetcher.jobs())
                }
                .build()
    }

}