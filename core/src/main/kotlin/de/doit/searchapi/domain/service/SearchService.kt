package de.doit.searchapi.domain.service

import de.doit.searchapi.domain.model.Job
import de.doit.searchapi.domain.model.JobId
import de.doit.searchapi.domain.model.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SearchService internal constructor(@Autowired private val jobRepository: JobRepository,
                                         @Autowired private val entityMapper: EntityMapper) {

    fun search(query: Query): Flow<Job> {
        return emptyFlow()
    }

    suspend fun byId(jobId: JobId): Job? {
        return jobRepository
                .findById(jobId)
                .map { entityMapper.map(it) }
                .awaitFirstOrNull()
    }

}