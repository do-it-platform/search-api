package de.doit.searchapi.web.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import de.doit.searchapi.domain.model.JobId
import de.doit.searchapi.domain.model.Query
import de.doit.searchapi.domain.service.SearchService
import de.doit.searchapi.web.model.JobResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.future
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
internal class QueryResolver(@Autowired private val searchService: SearchService,
                             @Autowired private val jobMapper: JobMapper) : GraphQLQueryResolver {

    fun job(id: String): CompletableFuture<JobResponse?> {
        return GlobalScope.future {
            searchService.byId(JobId(id))?.let { jobMapper.mapToDTO(it) }
        }
    }

    fun jobs(location: Location?, distance: Double, first: Int): CompletableFuture<List<JobResponse>> {
        return GlobalScope.future {
            searchService
                    .search(Query(location = location?.toQueryLocation(), distance = distance, size = first))
                    .map { jobMapper.mapToDTO(it) }
                    .toList()
        }
    }

    data class Location(val latitude: Double, val longitude: Double)
    fun Location.toQueryLocation() = Query.Location(latitude, longitude)
}