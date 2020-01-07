package de.doit.searchapi.domain.service

import de.doit.searchapi.domain.model.JobId
import org.springframework.data.repository.Repository
import reactor.core.publisher.Mono

@org.springframework.stereotype.Repository
internal interface JobRepository: Repository<JobEntity, String> {
    fun findById(id: String): Mono<JobEntity>
    @JvmDefault fun findById(jobId: JobId): Mono<JobEntity> = findById(jobId.value)
}
