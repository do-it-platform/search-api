package de.doit.searchapi.web.graphql

import de.doit.searchapi.domain.model.JobId
import de.doit.searchapi.domain.model.Query
import de.doit.searchapi.domain.model.Query.Location
import de.doit.searchapi.domain.service.SearchService
import de.doit.searchapi.web.model.JobResponse
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.future
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
internal class JobDataFetcher(@Autowired private val searchService: SearchService,
                              @Autowired private val jobMapper: JobMapper) {

    fun job(): DataFetcher<CompletableFuture<JobResponse?>> {
        return DataFetcher { dataFetchingEnvironment ->
            val jobId = JobId(dataFetchingEnvironment.getArgument("id"))

            GlobalScope.future {
                searchService.byId(jobId)?.let { jobMapper.mapToDTO(it) }
            }
        }
    }

    fun jobs(): DataFetcher<CompletableFuture<List<JobResponse>>> {
        return DataFetcher { dataFetchingEnvironment ->
            val location = dataFetchingEnvironment.getLocation()
            val distance: Double = dataFetchingEnvironment.getArgument("distance")

            GlobalScope.future {
                searchService
                        .search(Query(location = location, distance = distance))
                        .map { jobMapper.mapToDTO(it) }
                        .toList()
            }
        }
    }

    private fun DataFetchingEnvironment.getLocation(): Location? {
        return getArgument<Map<String, Double>>("location")?.let {
            Location(it.getValue("latitude"), it.getValue("longitude"))
        }
    }

}