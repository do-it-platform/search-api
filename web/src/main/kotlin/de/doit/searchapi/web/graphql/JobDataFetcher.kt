package de.doit.searchapi.web.graphql

import de.doit.searchapi.domain.model.JobId
import de.doit.searchapi.domain.model.Query
import de.doit.searchapi.domain.model.VendorId
import de.doit.searchapi.domain.service.SearchService
import de.doit.searchapi.web.model.Job
import graphql.schema.DataFetcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.future
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
internal class JobDataFetcher(@Autowired private val searchService: SearchService) {

    companion object {
        fun map(job: de.doit.searchapi.domain.model.Job): Job = Job(
                job.id,
                job.vendorId,
                job.title,
                job.description,
                job.location.lat,
                job.location.lon,
                job.payment.toEngineeringString(),
                job.createdAt.toString(),
                job.modifiedAt.toString()
        )
    }

    fun job(): DataFetcher<CompletableFuture<Job?>> {
        return DataFetcher { dataFetchingEnvironment ->
            val jobId = JobId(dataFetchingEnvironment.getArgument("id"))
            GlobalScope.future {
                searchService.byId(jobId)?.let { map(it) }
            }
        }
    }

    fun jobs(): DataFetcher<CompletableFuture<List<Job>>> {
        return DataFetcher { dataFetchingEnvironment ->
            val latitude: Double? = dataFetchingEnvironment.getArgument("latitude")
            val longitude: Double? = dataFetchingEnvironment.getArgument("longitude")
            GlobalScope.future {
                searchService
                        .search(Query(
                                latitude = latitude,
                                longitude = longitude
                        ))
                        .map { map(it) }
                        .toList()
            }
        }
    }

}