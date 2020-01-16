package de.doit.searchapi.domain.service

import de.doit.searchapi.domain.model.Job
import de.doit.searchapi.domain.model.JobId
import de.doit.searchapi.domain.model.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.elasticsearch.common.unit.DistanceUnit.KILOMETERS
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.index.query.QueryBuilders.geoDistanceQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Service

@Service
class SearchService internal constructor(@Autowired private val elasticsearchTemplate: ReactiveElasticsearchTemplate,
                                         @Autowired private val entityMapper: EntityMapper) {

    fun search(query: Query): Flow<Job> {
        return elasticsearchTemplate
                .find<JobEntity>(toEsQuery(query))
                .map { entityMapper.map(it) }
                .asFlow()
    }

    suspend fun byId(jobId: JobId): Job? {
        return elasticsearchTemplate
                .findById<JobEntity>(jobId)
                .map { entityMapper.map(it) }
                .awaitFirstOrNull()
    }

    companion object {

        fun toEsQuery(query: Query): NativeSearchQuery = QueryBuilders.boolQuery()
                .apply {
                    filterByLocation(query)
                }
                .let {
                    NativeSearchQueryBuilder()
                            .withQuery(it)
                            .withPageable(PageRequest.of(0, query.size))
                            .build()
                }

        private fun BoolQueryBuilder.filterByLocation(query: Query) {
            query.location
                    ?.let { buildDistanceQuery(it, query.distance) }
                    ?.run { filter(this) }
        }

        private fun buildDistanceQuery(location: Query.Location, distance: Double): QueryBuilder? {
            return geoDistanceQuery("location")
                    .point(location.lat, location.lon)
                    .distance(distance, KILOMETERS)
        }

    }

}